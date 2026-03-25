resource "kubernetes_deployment" "report_service_app" {

  depends_on = [
    kubernetes_namespace.report_service 
  ]

  metadata {
    name      = "report-service"
    namespace = kubernetes_namespace.report_service.metadata[0].name
  }

  wait_for_rollout = false

  spec {
    replicas = 2

    selector {
      match_labels = {
        app = "report-service"
      }
    }

    template {
      metadata {
        labels = {
          app = "report-service"
        }
        annotations = {
          "tags.datadoghq.com/env"     = "dev"
          "tags.datadoghq.com/service" = "report-service"
          "tags.datadoghq.com/version" = "1.0.0"
          "admission.datadoghq.com/enabled" = "true"
        }
      }

      spec {
        volume {
          name = "dd-java-agent"
          empty_dir {}
        }

        init_container {
          name  = "dd-java-agent-init"
          image = "curlimages/curl:8.10.1"
          command = ["sh","-c","curl -L -o /dd/dd-java-agent.jar https://dtdg.co/latest-java-tracer"]
          volume_mount {
            name       = "dd-java-agent"
            mount_path = "/dd"
          }
        }

        container {
          name              = "report-service"
          image             = "thiagotierre/report-service:latest"
          image_pull_policy = "Always"
          

          port {
            container_port = 8083
          }

          env_from {
              secret_ref {
                name = kubernetes_secret.report_service_secret.metadata[0].name
              }
            }

          env {
            name  = "SPRING_DATASOURCE_URL"
            value = "jdbc:postgresql://${data.terraform_remote_state.rds.outputs.rds_endpoint_host}:${data.terraform_remote_state.rds.outputs.db_port}/${data.terraform_remote_state.rds.outputs.db_name}?currentSchema=${var.db_schema}"
          }

          env {
            name  = "JAVA_TOOL_OPTIONS"
            value = "-javaagent:/dd/dd-java-agent.jar"
          }
          env {
            name  = "DD_SERVICE"
            value = "report-service"
          }
          env {
            name  = "DD_ENV"
            value = "dev"
          }
          env {
            name  = "DD_VERSION"
            value = "1.0.0"
          }
          env {
            name  = "DD_LOGS_INJECTION"
            value = "true"
          }
          env {
            name  = "DD_APPSEC_ENABLED"
            value = "true"
          }
          env {
            name  = "DD_IAST_ENABLED"
            value = "true"
          }
          env {
            name  = "DD_AGENT_HOST"
            value = "datadog-agent.datadog-agent.svc.cluster.local"
          }
          env {
            name  = "DD_DOGSTATSD_PORT"
            value = "8125"
          }
          env {
            name  = "DATADOG_STATSD_HOST"
            value = "datadog-agent.datadog-agent.svc.cluster.local"
          }
          env {
            name  = "DATADOG_STATSD_PORT"
            value = "8125"
          }

          volume_mount {
            name       = "dd-java-agent"
            mount_path = "/dd"
          }

          env {
            name  = "DB_SCHEMA"
            value = "public"
          }

          startup_probe {
            http_get {
              path = "/actuator/health"
              port = 8083
            }
            initial_delay_seconds = 120
            failure_threshold = 30
            period_seconds    = 10
          }

          liveness_probe {
            http_get {
              path = "/actuator/health"
              port = 8083
            }
            initial_delay_seconds = 60
            period_seconds  = 30
            timeout_seconds = 5
            failure_threshold = 3
          }

          readiness_probe {
            http_get {
              path = "/actuator/health"
              port = 8083
            }
            initial_delay_seconds = 60
            period_seconds        = 30
          }

          resources {
            requests = {
              cpu    = "50m"
              memory = "512Mi"
            }
            limits = {
              cpu    = "500m"
              memory = "1Gi"
            }
          }

          security_context {
            allow_privilege_escalation = false
            read_only_root_filesystem = false
          }
        }

        security_context {
          run_as_non_root = false
        }
      }
    }

    strategy {
      type = "RollingUpdate"
      rolling_update {
        max_unavailable = 1
        max_surge       = 0
      }
    }
  }
}
