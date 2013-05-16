#include "WebTile.h"

#if TRANSPARENT
#define TEX_FORMAT	GL_RGBA
#else
#define TEX_FORMAT	GL_RGB
#endif

WebTile::WebTile(int width, int height) : width(width), height(height), bpp(4), isTransparent(false)
{
	webView = Awesomium::WebCore::Get().createWebView(width, height);
	rowspan = width * bpp;
	buffer = new unsigned char[rowspan * height];
	
	glGenTextures(1, &textureID);
	glBindTexture(GL_TEXTURE_2D, textureID);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);	
	GLfloat largest_supported_anisotropy;
	glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, &largest_supported_anisotropy);
	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, largest_supported_anisotropy);
	glTexImage2D(GL_TEXTURE_2D, 0, TEX_FORMAT, width, height, 0, 
				 bpp == 3 ? GL_RGB : GL_BGRA, GL_UNSIGNED_BYTE, buffer);
}

WebTile::WebTile(Awesomium::WebView* existingWebView, int width, int height) : webView(existingWebView), width(width), height(height), bpp(4), isTransparent(false)
{
	rowspan = width * bpp;
	buffer = new unsigned char[rowspan * height];
	
	glGenTextures(1, &textureID);
	glBindTexture(GL_TEXTURE_2D, textureID);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);	
	GLfloat largest_supported_anisotropy;
	glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, &largest_supported_anisotropy);
	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, largest_supported_anisotropy);
	glTexImage2D(GL_TEXTURE_2D, 0, TEX_FORMAT, width, height, 0, 
				 bpp == 3 ? GL_RGB : GL_BGRA, GL_UNSIGNED_BYTE, buffer);
}

WebTile::~WebTile()
{
	glDeleteTextures(1, &textureID);
	delete[] buffer;
	webView->destroy();
}

void WebTile::resize(int width, int height)
{
	glDeleteTextures(1, &textureID);
	delete[] buffer;
	
	this->width = width;
	this->height = height;
	
	rowspan = width * bpp;
	buffer = new unsigned char[rowspan * height];
	
	glGenTextures(1, &textureID);
	glBindTexture(GL_TEXTURE_2D, textureID);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);	
	GLfloat largest_supported_anisotropy;
	glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, &largest_supported_anisotropy);
	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, largest_supported_anisotropy);
	glTexImage2D(GL_TEXTURE_2D, 0, TEX_FORMAT, width, height, 0, 
				 bpp == 3 ? GL_RGB : GL_BGRA, GL_UNSIGNED_BYTE, buffer);
	webView->resize(width, height);
}

void WebTile::toggleTransparency()
{
	webView->executeJavascript("document.body.style.backgroundColor = 'transparent'");
	webView->setTransparent(isTransparent = !isTransparent);
}