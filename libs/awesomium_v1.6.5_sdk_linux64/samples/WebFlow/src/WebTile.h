#ifndef __WEB_TILE_H__
#define __WEB_TILE_H__

#include "Application.h"

// A "WebTile" is essentially a WebView assigned to an OpenGL texture.
struct WebTile
{
	GLuint textureID;
	unsigned char* buffer;
	int bpp, rowspan, width, height;
	Awesomium::WebView* webView;
	bool isTransparent;
	
	WebTile(int width, int height);
	WebTile(Awesomium::WebView* existingWebView, int width, int height);
	~WebTile();
	
	void resize(int width, int height);
	void toggleTransparency();
};

#endif