variable "projectName" {
  default = "challengeone-g19"
}

variable "environment" {
  type = string
  default = "develop"
}

variable "region_default" {
  default = "us-east-2"
}

variable "db_password" {
  type      = string
  sensitive = true
}

variable "db_schema" {
  type = string
  default = "public"
}

variable "tags" {
  default = {
    Name = "g19-challengeone"
  }
  
}