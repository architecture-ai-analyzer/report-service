resource "kubernetes_service" "report_service" {
  metadata {
    name      = "report-service"
    namespace = kubernetes_namespace.report_service.metadata[0].name
    labels = {
      app = "report-service"
    }

    annotations = {
      "service.beta.kubernetes.io/aws-load-balancer-type"            = "nlb"
      "service.beta.kubernetes.io/aws-load-balancer-nlb-target-type" = "instance"
      "service.beta.kubernetes.io/aws-load-balancer-scheme"          = "internal"
      "service.beta.kubernetes.io/aws-load-balancer-manage-backend-security-group-rules" = "true"
    }
  }

  spec {
    type = "LoadBalancer"

    selector = {
      app = "report-service"
    }

    port {
      name        = "http"
      protocol    = "TCP"
      port        = 80
      target_port = 8080
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
