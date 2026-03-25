resource "kubernetes_service" "report_service_app" {
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
    selector = {
      app = "report-service"
    }

    port {
      protocol    = "TCP"
      port        = 80
      target_port = 8083
    }

    type = "LoadBalancer"
  }
}
