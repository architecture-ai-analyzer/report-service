resource "kubernetes_service_account" "report_service" {
  metadata {
    name      = var.app_name
    namespace = kubernetes_namespace.report_service.metadata[0].name
  }

  depends_on = [
    kubernetes_namespace.report_service
  ]
}
