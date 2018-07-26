### Do not change this section of the file ###
STATFLO_COMMON_DIR=~/.statflo
MAKEFILE_REPO=git@github.com:Statflo/makefiles.git
MAKEFILE_DIR=$(STATFLO_COMMON_DIR)/makefiles

$(shell [ -d $(STATFLO_COMMON_DIR) ] || mkdir $(STATFLO_COMMON_DIR))
$(shell [ -f $(MAKEFILE_DIR)/targets.mk ] || git clone $(MAKEFILE_REPO) $(MAKEFILE_DIR))

-include overrides.mk

include $(MAKEFILE_DIR)/targets.mk

### Application specific targets can be added to this file ###
-include targets.mk

### Or application sepcific targets can be added here ###