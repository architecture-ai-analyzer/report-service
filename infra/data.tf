data "aws_eks_cluster" "cluster" {
  name = data.terraform_remote_state.infra.outputs.cluster_name
}

data "aws_eks_cluster_auth" "main" {
  name = data.aws_eks_cluster.cluster.name
}
