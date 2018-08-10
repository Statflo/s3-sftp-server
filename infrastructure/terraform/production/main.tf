locals {
  domain           = "${lookup(var.cluster_domain, var.region)}"
  namespace_domain = "${lookup(var.common_namespace_domain, var.region)}"
}

provider "aws" {
  version = "~> 1.18"
  region  = "${var.region}"
}

resource "aws_iam_user" "processing_s3_sftp_server" {
  name = "s3-sftp-server"
}

resource "aws_iam_policy" "processing_s3_sftp_server" {
  name        = "sftp_server.ca.prod.stflo.io"
  description = "S3 policy for s3_sftp_server"
  policy      = "${data.aws_iam_policy_document.processing_s3_sftp_server.json}"
}

resource "aws_iam_user_policy_attachment" "processing_s3_sftp_server" {
  user       = "${aws_iam_user.processing_s3_sftp_server.name}"
  policy_arn = "${aws_iam_policy.processing_s3_sftp_server.arn}"
}

data "aws_iam_policy_document" "processing_s3_sftp_server" {
  statement {
    sid = "1"

    actions = [
      "s3:ListAllMyBuckets",
      "s3:GetBucketLocation",
    ]

    resources = [
      "arn:aws:s3:::*",
    ]
  }

  statement {
    actions = [
      "s3:DeleteObject",
      "s3:GetObject",
      "s3:ListBucket",
      "s3:PutObject",
      "s3:PutObjectAcl",
    ]

    resources = [
      "arn:aws:s3:::calllist-return-files/*",
      "arn:aws:s3:::statflo-ftp-*/*",
      "arn:aws:s3:::statflo-ftp/*",
      "arn:aws:s3:::prod-statflo-ftp-ca1/*",
    ]
  }

  statement {
    actions = [
      "s3:ListBucket",
    ]

    resources = [
      "arn:aws:s3:::calllist-return-files",
      "arn:aws:s3:::statflo-ftp-*",
      "arn:aws:s3:::statflo-ftp",
      "arn:aws:s3:::prod-statflo-ftp-ca1",
    ]
  }
}
