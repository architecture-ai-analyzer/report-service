output "k8s_namespace" {
  description = "Kubernetes namespace for report-service"
  value       = kubernetes_namespace.report_service.metadata[0].name
}

output "k8s_deployment_name" {
  description = "Kubernetes deployment name"
  value       = kubernetes_deployment.report_service.metadata[0].name
}

output "k8s_service_name" {
  description = "Kubernetes service name"
  value       = kubernetes_service.report_service.metadata[0].name
}

output "k8s_service_cluster_ip" {
  description = "Kubernetes service cluster IP"
  value       = kubernetes_service.report_service.spec[0].cluster_ip
}

output "k8s_hpa_name" {
  description = "Kubernetes HPA name"
  value       = kubernetes_horizontal_pod_autoscaler.report_service.metadata[0].name
}

output "k8s_service_account_name" {
  description = "Kubernetes service account name"
  value       = kubernetes_service_account.report_service.metadata[0].name
}

output "loadbalancer_hostname" {
  description = "Hostname do Load Balancer"
  value       = kubernetes_service.report_service.status[0].load_balancer[0].ingress[0].hostname
}
