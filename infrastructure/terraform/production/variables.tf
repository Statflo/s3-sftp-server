variable "region" {
  description = "AWS Subnet Region"
}

variable "cluster_domain" {
  type        = "map"
  description = "Cluster Domain"
}

########################################
### Database Server
########################################
variable "common_namespace_domain" {
  type        = "map"
  description = "Common Domain"
}

variable "common_database_port" {
  type        = "map"
  description = "Common database port"
}

variable "common_database_root_password" {
  type        = "map"
  description = "Common database root password"
}

variable "common_database_root_username" {
  type        = "map"
  description = "Common database root username"
}

variable "common_database_instance_class" {
  type        = "map"
  description = "Common database instances class"
}

variable "common_database_write_domain" {
  type        = "map"
  description = "Common database write URI"
}

variable "common_database_read_domain" {
  type        = "map"
  description = "Common database read URI"
}

########################################
### Translation Database
########################################

variable "common_database_translation_name" {
  type        = "map"
  description = ""
}

variable "common_database_translation_username" {
  type        = "map"
  description = ""
}

variable "common_database_translation_password" {
  type        = "map"
  description = ""
}
