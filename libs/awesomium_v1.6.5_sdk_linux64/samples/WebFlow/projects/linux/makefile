
BASE_DIR := ../../../..
OUT_DIR := $(BASE_DIR)/build/bin/release

RM := rm -rf

LIB_NAME := libawesomium-1.6.5.so
LIB_SONAME := libawesomium-1.6.5.so.0
LIB_LINK_NAME := -lawesomium-1.6.5

all: $(OUT_DIR)/WebFlow

$(OUT_DIR)/WebFlow: $(OUT_DIR)/$(LIB_NAME) $(OUT_DIR)/AwesomiumProcess
	@echo 'Building target: $@'
	g++ -w -I$(BASE_DIR)/include $(BASE_DIR)/samples/WebFlow/src/main.cpp $(BASE_DIR)/samples/WebFlow/src/Application.cpp $(BASE_DIR)/samples/WebFlow/src/WebTile.cpp `sdl-config --cflags --libs` -L$(OUT_DIR) $(LIB_LINK_NAME) -lGL -lGLU -o $(OUT_DIR)/WebFlow
	@echo 'Finished building target: $@'
	@echo ' '

clean: 
	-$(RM) $(OUT_DIR)/WebFlow
	-$(RM) $(OUT_DIR)/WebFlow
	-@echo ' '
