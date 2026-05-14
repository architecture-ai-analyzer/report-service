resource "kubernetes_service_account" "report_service" {
  metadata {
    name      = "report-service"
    namespace = kubernetes_namespace.report_service.metadata[0].name
  }

  depends_on = [
    kubernetes_namespace.report_service
  ]
}
