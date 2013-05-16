
BASE_DIR := ../../..
OUT_DIR := $(BASE_DIR)/build/bin/release

RM := rm -rf

LIB_NAME := libawesomium-1.6.5.so
LIB_SONAME := libawesomium-1.6.5.so.0
LIB_LINK_NAME := -lawesomium-1.6.5

all: $(OUT_DIR)/HelloAwesomium $(OUT_DIR)/AwesomiumGL

$(OUT_DIR)/HelloAwesomium: $(OUT_DIR)/$(LIB_NAME) $(OUT_DIR)/AwesomiumProcess
	@echo 'Building target: $@'
	g++ -w -I$(BASE_DIR)/include $(BASE_DIR)/samples/HelloAwesomium/main.cpp -L$(OUT_DIR) $(LIB_LINK_NAME) -o $(OUT_DIR)/HelloAwesomium
	@echo 'Finished building target: $@'
	@echo ' '

$(OUT_DIR)/AwesomiumGL: $(OUT_DIR)/$(LIB_NAME) $(OUT_DIR)/AwesomiumProcess
	@echo 'Building target: $@'
	g++ -w -I$(BASE_DIR)/include $(BASE_DIR)/samples/AwesomiumGL/main.cpp -L$(OUT_DIR) $(LIB_LINK_NAME) -lglut -lGL -lGLU -o $(OUT_DIR)/AwesomiumGL
	@echo 'Finished building target: $@'
	@echo ' '

clean: 
	-$(RM) $(OUT_DIR)/HelloAwesomium
	-$(RM) $(OUT_DIR)/AwesomiumGL
	-@echo ' '
