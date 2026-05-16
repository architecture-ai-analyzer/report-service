/*# ===============================================
# SERVICE - LOAD BALANCER (NLB)
# ===============================================

resource "kubernetes_service" "report_service" {
  metadata {
    name      = "${var.app_name}-service"
    namespace = kubernetes_namespace.report_service.metadata[0].name

    labels = {
      app         = var.app_name
      service     = var.service
      environment = var.environment
    }

    annotations = {
      "service.beta.kubernetes.io/aws-load-balancer-type"            = "nlb"
      "service.beta.kubernetes.io/aws-load-balancer-internal"        = "true"
      "service.beta.kubernetes.io/aws-load-balancer-name"            = "${var.service}-service-lb-dev"
      "service.beta.kubernetes.io/aws-load-balancer-scheme"          = "internal"
      "service.beta.kubernetes.io/aws-load-balancer-cross-zone-load-balancing-enabled" = "true"

      # Health Check Annotations
      "service.beta.kubernetes.io/aws-load-balancer-healthcheck-protocol"          = "HTTP"
      "service.beta.kubernetes.io/aws-load-balancer-healthcheck-path"              = var.health_check_path
      "service.beta.kubernetes.io/aws-load-balancer-healthcheck-port"              = tostring(var.app_port)
      "service.beta.kubernetes.io/aws-load-balancer-healthcheck-interval"          = "30"
      "service.beta.kubernetes.io/aws-load-balancer-healthcheck-timeout"           = "10"
      "service.beta.kubernetes.io/aws-load-balancer-healthcheck-healthy-threshold" = "2"
      "service.beta.kubernetes.io/aws-load-balancer-healthcheck-unhealthy-threshold" = "2"
    }
  }

  spec {
    type = "LoadBalancer"

    selector = {
      app = var.app_name
    }

    port {
      name        = "http"
      port        = 80
      target_port = var.app_port
      protocol    = "TCP"
    }

    session_affinity = "None"
  }

  # Wait for Load Balancer to be ready
  wait_for_load_balancer = true

  timeouts {
    create = "10m"
  }
}
*/