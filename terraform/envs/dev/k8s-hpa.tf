resource "kubernetes_horizontal_pod_autoscaler" "report_service" {
  metadata {
    name      = "report-service-hpa"
    namespace = kubernetes_namespace.report_service.metadata[0].name
    labels = {
      app = "report-service"
    }
  }

  spec {
    max_replicas = 5
    min_replicas = 2

    scale_target_ref {
      api_version = "apps/v1"
      kind        = "Deployment"
      name        = kubernetes_deployment.report_service.metadata[0].name
    }

    # CPU-based scaling (70% target utilization)
    target_cpu_utilization_percentage = 70
  }

  depends_on = [
    kubernetes_deployment.report_service
  ]
}
