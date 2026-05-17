resource "kubernetes_service" "report_service" {
  metadata {
    name      = var.app_name
    namespace = kubernetes_namespace.report_service.metadata[0].name
    labels = {
      app = var.app_name
    }

    annotations = {
      "service.beta.kubernetes.io/aws-load-balancer-type"                                = "nlb"
      "service.beta.kubernetes.io/aws-load-balancer-nlb-target-type"                     = "instance"
      "service.beta.kubernetes.io/aws-load-balancer-scheme"                              = "internal"
      "service.beta.kubernetes.io/aws-load-balancer-manage-backend-security-group-rules" = "true"
    }
  }

  spec {
    type = "LoadBalancer"

    selector = {
      app = var.app_name
    }

    port {
      name        = "http"
      protocol    = "TCP"
      port        = 80
      target_port = var.app_port
    }

    session_affinity = "None"
  }

  wait_for_load_balancer = true

  timeouts {
    create = "10m"
  }

  depends_on = [
    kubernetes_deployment.report_service
  ]
}
