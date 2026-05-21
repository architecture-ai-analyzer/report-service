resource "kubernetes_deployment" "report_service" {
  depends_on = [
    kubernetes_namespace.report_service,
    kubernetes_service_account.report_service,
    kubernetes_config_map.report_service,
    kubernetes_secret.report_service,
  ]

  metadata {
    name      = var.app_name
    namespace = kubernetes_namespace.report_service.metadata[0].name
    labels = {
      app     = var.app_name
      version = "v1"
    }
  }

  wait_for_rollout = false

  spec {
    replicas = 2

    selector {
      match_labels = {
        app = var.app_name
      }
    }

    template {
      metadata {
        labels = {
          app     = var.app_name
          version = "v1"
        }

        annotations = {
          "prometheus.io/scrape" = "true"
          "prometheus.io/port"   = "8080"
        }
      }

      spec {
        service_account_name = kubernetes_service_account.report_service.metadata[0].name

        container {
          name              = var.app_name
          image             = var.container_image
          image_pull_policy = "Always"

          port {
            name           = "http"
            container_port = 8080
            protocol       = "TCP"
          }

          env_from {
            config_map_ref {
              name = kubernetes_config_map.report_service.metadata[0].name
            }
          }

          env_from {
            secret_ref {
              name = kubernetes_secret.report_service.metadata[0].name
            }
          }

          liveness_probe {
            http_get {
              path   = "/actuator/health"
              port   = 8080
              scheme = "HTTP"
            }

            initial_delay_seconds = 60
            period_seconds        = 30
            timeout_seconds       = 5
            failure_threshold     = 3
          }

          readiness_probe {
            http_get {
              path   = "/actuator/health/readiness"
              port   = 8080
              scheme = "HTTP"
            }

            initial_delay_seconds = 30
            period_seconds        = 10
            timeout_seconds       = 5
            failure_threshold     = 3
          }

          resources {
            requests = {
              cpu    = "250m"
              memory = "256Mi"
            }

            limits = {
              cpu    = "500m"
              memory = "512Mi"
            }
          }

          volume_mount {
            name       = "tmp"
            mount_path = "/tmp"
          }
        }

        volume {
          name = "tmp"
          empty_dir {}
        }

        security_context {
          run_as_non_root = true
          run_as_user     = 1000
          fs_group        = 2000
        }

        restart_policy = "Always"
      }
    }
  }
}
