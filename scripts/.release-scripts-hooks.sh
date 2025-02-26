#!/usr/bin/env bash
#
# Copyright 2018-2022 adorsys GmbH & Co KG
#
# This program is free software: you can redistribute it and/or modify it
# under the terms of the GNU Affero General Public License as published
# by the Free Software Foundation, either version 3 of the License, or (at
# your option) any later version. This program is distributed in the hope that
# it will be useful, but WITHOUT ANY WARRANTY; without even the implied
# warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program. If not, see https://www.gnu.org/licenses/.
#
# This project is also available under a separate commercial license. You can
# contact us at psd2@adorsys.com.
#

# ********************** INFO *********************
# This file is used to override default settings.
# Therefore only the functions that deviate from
# provided defaults may be left here.
# You can use it also in parent directory with the
# name .release-scripts-hook.sh
# *************************************************
set -e


# Hook method to format your release tag
# Parameter $1 - version as text
# Returns tag as text
function format_release_tag {
  echo "$1"
}

# Hook method to format your next snapshot version
# Parameter $1 - version as text
# Returns snapshot version as text
function format_snapshot_version {
  echo "$1-SNAPSHOT"
}

# Hook method to define the remote repository name
# Returns the name of the remote repository as text
function get_remote_repo_name {
  echo "origin"
}

# Hook method to define the develop branch name
# Returns the develop branch name as text
function get_develop_branch_name {
  if [[ "$1" =~ ^14\..* ]]
  then
    echo "support-14.x"
  else
    echo "develop"
  fi
}

# Hook method to define the master branch name
# Returns the master branch name as text
function get_master_branch_name {
  if [[ "$1"  =~ ^14\..* ]]
  then
    echo "master-14.x"
  else
    echo "master"
  fi
}

# Hook method to format the release branch name
# Parameter $1 - version as text
# Returns the formatted release branch name as text
function format_release_branch_name {
  echo "release-$1"
}

# Hook method to format the hotfix branch name
# Parameter $1 - version as text
# Returns the formatted hotfix branch name as text
function format_hotfix_branch_name {
  echo "hotfix-$1"
}

# Hook to build the snapshot modules before release
# You can build and run your tests here to avoid releasing an unstable build
function build_snapshot_modules {
	mvn clean install
	mvn pmd:check
}

# Hook to build the released modules after release
# You can deploy your artifacts here
function build_release_modules {
	mvn clean install
	mvn pmd:check
}

# Should set version numbers in your modules
# Parameter $1 - version as text
function set_modules_version {
  mvn versions:set -DnewVersion="$1"
}
