terraform {
  backend "s3" {
    bucket  = "tf.ca.prod.stflo.io"
    key     = "processing/sftpd.tfstate"
    region  = "ca-central-1"
    encrypt = true
  }
}
