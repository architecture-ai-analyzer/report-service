resource "kubernetes_namespace" "report_service" {
  metadata {
    name = "report-service-${var.environment}"
  }
}
