#!/bin/bash
set -e

# vagrant specific variables
vagrant_dir=/vagrant/vagrant
bashrc=/home/vagrant/.bashrc

# hm-teller specific variables
aws_key=
aws_secret=
fb_app_id=
fb_secret=
google_app_id=
google_secret=
linkedin_key=
linkedin_secret=
twitter_key=
twitter_secret=
memcached_url=127.0.0.1:112111
memcached_username=happymelly
memcached_password=

echo "========================================"
echo "INSTALLING PERU AND ANSIBLE DEPENDENCIES"
echo "----------------------------------------"
apt-get update
apt-get install -y language-pack-en git unzip libyaml-dev python3-pip python-yaml python-paramiko python-jinja2

echo "==============="
echo "INSTALLING PERU"
echo "---------------"
sudo pip3 install peru

echo "======================================="
echo "CLONING ANSIBLE AND PLAYBOOKS WITH PERU"
echo "---------------------------------------"
cd ${vagrant_dir} && peru sync -v
echo "... done"

env_setup=${vagrant_dir}/ansible/hacking/env-setup
hosts=${vagrant_dir}/ansible.hosts

echo "==================="
echo "CONFIGURING ANSIBLE"
echo "-------------------"
touch ${bashrc}
echo "source ${env_setup}" >> ${bashrc}
echo "export ANSIBLE_HOSTS=${hosts}" >> ${bashrc}
echo "... done"

echo "=========================================="
echo "CONFIGURE HAPPY MELLY ENV VARS"
echo "------------------------------------------"
echo "export AWS_ACCESS_KEY_ID=${aws_key}" >> ${bashrc}
echo "export AWS_SECRET_KEY=${aws_secret}" >> ${bashrc}
echo "export TWITTER_KEY=${twitter_key}" >> ${bashrc}
echo "export TWITTER_SECRET=${twitter_secret}" >> ${bashrc}
echo "export FACEBOOK_ID=${fb_app_id}" >> ${bashrc}
echo "export FACEBOOK_SECRET=${fb_secret}" >> ${bashrc}
echo "export GOOGLE_ID=${google_app_id}" >> ${bashrc}
echo "export GOOGLE_SECRET=${google_secret}" >> ${bashrc}
echo "export LINKEDIN_KEY=${linkedin_key}" >> ${bashrc}
echo "export LINKEDIN_SECRET=${linkedin_secret}" >> ${bashrc}
echo "export MEMCACHIER_SERVERS=${memcached_url}" >> ${bashrc}
echo "export MEMCACHIER_USERNAME=${memcached_username}" >> ${bashrc}
echo "export MEMCACHIER_PASSWORD=${memcached_password}" >> ${bashrc}
echo "... done"

echo "=========================================="
echo "RUNNING PLAYBOOKS WITH ANSIBLE*"
echo "* no output while each playbook is running"
echo "------------------------------------------"
while read pb; do
 su - -c "source ${env_setup} && ${vagrant_dir}/ansible/bin/ansible-playbook ${vagrant_dir}/${pb} --connection=local --inventory-file=${hosts}" vagrant
done <${vagrant_dir}/up.playbooks
