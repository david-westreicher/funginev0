#include "Application.h"
#include "WebTile.h"
#include <math.h>
#include <cmath>
#include <iostream>
#include <fstream>

// The vertices of our 3D quad
const GLfloat GVertices[] = {
	-1.0f, -1.0f, 0.0f,
	1.0f, -1.0f, 0.0f,
	-1.0f,  1.0f, 0.0f,
	1.0f,  1.0f, 0.0f,
};

// The UV texture coordinates of our 3D quad
const GLshort GTextures[] = {
	0, 1,
	1, 1,
	0, 0,
	1, 0,
};

Application::Application() : shouldQuit(false), webCore(0), offset(0), 
isAnimating(false), isDragging(false), numTiles(0), activeWebTile(-1), 
isActiveWebTileFocused(false), zoomStart(-1)
{
	int sdlError = SDL_Init(SDL_INIT_EVERYTHING);
	
	if(sdlError == -1)
	{
		shouldQuit = true;
		return;
	}
	
	const SDL_VideoInfo* info = SDL_GetVideoInfo(); 
	WIDTH = static_cast<int>(info->current_w * 0.7); 
	HEIGHT = static_cast<int>(info->current_h * 0.7);
	
	if(WIDTH > 1280)
		WIDTH = 1280;
	if(HEIGHT > 800)
		HEIGHT = 800;
	
	SDL_Init(SDL_INIT_VIDEO);
	SDL_WM_SetCaption("Awesomium v1.6 - WebFlow Demo","");
	SDL_GL_SetAttribute(SDL_GL_DOUBLEBUFFER, 1);
	SDL_GL_SetAttribute(SDL_GL_SWAP_CONTROL, 1);
	SDL_SetVideoMode(WIDTH, HEIGHT, 0, SDL_OPENGL);
	SDL_EnableUNICODE(1);
	
	gluOrtho2D(0, WIDTH, 0, HEIGHT);
	glEnable(GL_TEXTURE_2D);
	
	Awesomium::WebCoreConfig conf;
	conf.setEnablePlugins(true);
	conf.setSaveCacheAndCookies(false);
	conf.setLogLevel(Awesomium::LOG_VERBOSE);

#ifdef __linux__
	// We have to define our own scrollbars on Linux
	conf.setCustomCSS("::-webkit-scrollbar {width: 12px; height: 12px; background-color: #f2f2f1; } ::-webkit-scrollbar-track { border-radius: 10px; border: 1px solid #bbb7b3; background: -webkit-gradient(linear, left top, right top, color-stop(0, #dcd9d7), color-stop(1, #e5e3e2)); } ::-webkit-scrollbar-thumb {  border-radius: 10px; -webkit-box-shadow: inset 1px 0 0 1px white; border: 1px solid #9c9996; background: -webkit-gradient(linear, left top, right top, color-stop(0, #f9f9f8), color-stop(1, #e6e4e3)); } ::-webkit-scrollbar-track-piece:disabled { display: none !important; } ::-webkit-scrollbar-track:disabled { margin: 6px; }");
#endif
	
	webCore = new Awesomium::WebCore(conf);
	
	addWebTileWithURL("http://www.khrona.com/webflow/1.6rc3/webflow_intro.html", WIDTH, HEIGHT);
	addWebTileWithURL("http://www.google.com", WIDTH, HEIGHT);
	addWebTileWithURL("http://www.flickr.com/explore/interesting/7days/", WIDTH, HEIGHT);
	addWebTileWithURL("http://www.awesomium.com", WIDTH, HEIGHT);
	addWebTileWithURL("http://www.twitter.com", WIDTH, HEIGHT);
	
	// Set our first WebTile as active
	activeWebTile = 0;
	webTiles[0]->webView->focus();
	double curTime = SDL_GetTicks() / 1000.0;
	zoomDirection = true;
	zoomStart = curTime;
	zoomEnd = curTime + ZOOMTIME;
}

Application::~Application()
{
	for(int i = 0; i < webTiles.size(); i++)
		delete webTiles[i];
	
	if(webCore)
		delete webCore;
	
	SDL_Quit();
}

void Application::addWebTileWithURL(const std::string& url, int width, 
									int height)
{
	WebTile* tile = new WebTile(width, height);
	
	tile->webView->loadURL(url);
	tile->webView->setListener(this);
	
	webTiles.push_back(tile);
}

void Application::addWebTileWithHTML(const std::string& html, int width, 
									 int height)
{
	WebTile* tile = new WebTile(width, height);
	
	tile->webView->loadHTML(html);
	tile->webView->setListener(this);
	
	webTiles.push_back(tile);
}

void Application::update()
{
	handleInput();
	webCore->update();
	updateWebTiles();
	
	if(isAnimating)
		driveAnimation();
	
	draw();
	
	SDL_Delay(0);
}

void Application::draw()
{
	double curTime = SDL_GetTicks() / 1000.0;
	double zoom = 0;
	
	if(zoomStart > 0)
	{
		if(curTime < zoomEnd)
		{
			zoom = (curTime - zoomStart) / (zoomEnd - zoomStart);
			
			if(!zoomDirection)
				zoom = 1.0 - zoom;
		}
		else
		{
			zoomStart = -1;
			zoomEnd = 0;
			
			isActiveWebTileFocused = zoomDirection;
		}
	}
	
	if(isActiveWebTileFocused)
	{
		int tileWidth = webTiles[activeWebTile]->width;
		int tileHeight = webTiles[activeWebTile]->height;
		
		gluOrtho2D(0, WIDTH, 0, HEIGHT);
		glClear(GL_COLOR_BUFFER_BIT);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glScalef(1,1,1);
		glBindTexture(GL_TEXTURE_2D, webTiles[activeWebTile]->textureID);
		glColor4f(1,1,1,1);
		glBegin(GL_QUADS);
		glTexCoord2f(0,1);
		glVertex3f(0, 0, 0.0f);
		glTexCoord2f(1,1);
		glVertex3f((GLfloat)tileWidth, 0, 0.0f);
		glTexCoord2f(1,0);
		glVertex3f((GLfloat)tileWidth, (GLfloat)tileHeight, 0.0f);
		glTexCoord2f(0,0);
		glVertex3f(0, (GLfloat)tileHeight, 0.0f);
		glEnd();
		SDL_GL_SwapBuffers();
	}
	else
	{
		glViewport(0,0,WIDTH,HEIGHT);
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);
		glClearColor(0,0,0,0);
		glVertexPointer(3,GL_FLOAT,0,GVertices);
		glEnableClientState(GL_VERTEX_ARRAY);
		glTexCoordPointer(2, GL_SHORT, 0, GTextures);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		glColorPointer(4, GL_FLOAT, 0, customColor);
		glEnableClientState(GL_COLOR_ARRAY);
		glEnable(GL_TEXTURE_2D);
#if TRANSPARENT
		glEnable (GL_BLEND);
		glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
#endif
		glClear(GL_COLOR_BUFFER_BIT);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glScalef(1,1,1);
		glTranslatef(0, 0, 0.5);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		
		int i, len = webTiles.size();
		int mid = (int)floor(offset + 0.5);
		int iStartPos = mid - webTiles.size();
		
		if(iStartPos < 0)
			iStartPos = 0;
		
		for(i = iStartPos; i < mid; ++i)
			drawTile(i, i-offset, 0);
		
		int iEndPos = mid + webTiles.size();
		
		if(iEndPos >= len)
			iEndPos = len - 1;
		
		for(i = iEndPos; i > mid; --i)
			drawTile(i, i - offset, 0);
		
		drawTile(mid, mid - offset, zoom);
		
		SDL_GL_SwapBuffers();
	}
}

void Application::drawTile(int index, double off, double zoom)
{
	GLfloat m[16];
	memset(m,0,sizeof(m));
	m[10] = 1;
	m[15] = 1;
	m[0] = 1;
	m[5] = 1;
	
	double trans = off * SPREADIMAGE;
	double f = off * FLANKSPREAD;
	
	if(f < -FLANKSPREAD)
		f = -FLANKSPREAD;
	
	else if (f > FLANKSPREAD)
		f = FLANKSPREAD;
	
	m[3] = -f;
	m[0] = 1-fabs(f);
	
	double sc = 0.45 * (1 - fabs(f));
	sc = (1 - zoom) * sc + 1 * zoom;
	
	trans += f * 1.1;
	
	for(int i = 0; i < 16; i++)
		customColor[i] = 1.0;
	
	if(f >= 0)
	{
		customColor[0] = customColor[1] = customColor[2] = 1 - 
		(f / FLANKSPREAD);
		customColor[8] = customColor[9] = customColor[10] = 1 - 
		(f / FLANKSPREAD);
	}
	else
	{
		customColor[4] = customColor[5] = customColor[6] = 1 - 
		(-f / FLANKSPREAD);
		customColor[12] = customColor[13] = customColor[14] = 1 - 
		(-f / FLANKSPREAD);
	}
	
	
	glPushMatrix();
	glBindTexture(GL_TEXTURE_2D, webTiles[index]->textureID);
	glTranslatef(trans, 0, 0);
	glScalef(sc, sc, 1);
	glMultMatrixf(m);
	glColorPointer(4, GL_FLOAT, 0, customColor);
	glDrawArrays(GL_TRIANGLE_STRIP,0,4);
	
	// Draw reflection:
	
	glTranslatef(0,-2,0);
	glScalef(1,-1,1);
	
	for(int i = 0; i < 16; i += 4)
	{
		customColor[i] = 0.25;
		customColor[i + 1] = 0.25;
		customColor[i + 2] = 0.25;
	}
	
	if(f >= 0)
	{
		customColor[0] = customColor[1] = customColor[2] = 
		(1- (f / FLANKSPREAD)) / 5.0 + 0.05;
		
	}
	else
	{
		customColor[4] = customColor[5] = customColor[6] = 
		(1-(-f / FLANKSPREAD)) / 5.0 + 0.05;
		
	}
	
	customColor[8] = customColor[9] = customColor[10] = 0;
	customColor[12] = customColor[13] = customColor[14] = 0;
	
	glDrawArrays(GL_TRIANGLE_STRIP,0,4);
	
	glPopMatrix();
}

void Application::updateAnimationAtTime(double elapsed)
{
	int max = webTiles.size() - 1;
	
	if (elapsed > runDelta)
		elapsed = runDelta;
	
	double delta = fabs(startSpeed) * elapsed - FRICTION * elapsed * 
	elapsed / 2;
	
	if (startSpeed < 0)
		delta = -delta;
	
	offset = startOff + delta;
	
	if (offset > max)
		offset = max;
	
	if (offset < 0)
		offset = 0;
}

void Application::endAnimation()
{
	if (isAnimating)
	{
		int max = webTiles.size() - 1;
		offset = floor(offset + 0.5);
		
		if (offset > max)
			offset = max;
		
		if (offset < 0)
			offset = 0;
		
		isAnimating = false;
	}
}

void Application::driveAnimation()
{
	double elapsed = SDL_GetTicks() / 1000.0 - startTime;
	
	if (elapsed >= runDelta)
		endAnimation();
	else
		updateAnimationAtTime(elapsed);
}

void Application::startAnimation(double speed)
{
	if(isAnimating)
		endAnimation();
	
	// Adjust speed to make this land on an even location
	double delta = speed * speed / (FRICTION * 2);
	if (speed < 0)
		delta = -delta;
	
	double nearest = startOff + delta;
	nearest = floor(nearest + 0.5);
	startSpeed = sqrt(fabs(nearest - startOff) * FRICTION * 2);
	
	if (nearest < startOff)
		startSpeed = -startSpeed;
	
	runDelta = fabs(startSpeed / FRICTION);
	startTime = SDL_GetTicks() / 1000.0;
	
	isAnimating = true;
	
	int lastActiveWebTile = activeWebTile;
	
	activeWebTile = (int)nearest;
	
	if(activeWebTile >= (int)webTiles.size())
		activeWebTile = webTiles.size() - 1;
	else if(activeWebTile < 0)
		activeWebTile = 0;
	
	if(activeWebTile != lastActiveWebTile)
	{
		webTiles[lastActiveWebTile]->webView->unfocus();
		webTiles[lastActiveWebTile]->webView->pauseRendering();
		webTiles[activeWebTile]->webView->focus();
		webTiles[activeWebTile]->webView->resumeRendering();
	}
}

void Application::animateTo(int index)
{
	if(index == offset)
		return;
	
	if(isActiveWebTileFocused)
	{
		double curTime = SDL_GetTicks() / 1000.0;
		zoomDirection = false;
		zoomStart = curTime;
		zoomEnd = curTime + ZOOMTIME;
		isActiveWebTileFocused = false;
	}
	
	startOff = offset;
	offset = index;
	
	int dist = (int)offset - (int)startOff;
	
	double speed = sqrt(abs(dist) * 2 * FRICTION);
	
	if(dist < 0)
		speed = -speed;
	
	startAnimation(speed);
}

void Application::updateWebTiles()
{
	for(std::vector<WebTile*>::iterator i = webTiles.begin(); i != 
		webTiles.end(); ++i)
	{
		if(!(*i)->webView->isDirty())
			continue;
		
		const Awesomium::RenderBuffer* buffer = 
		(*i)->webView->render();
		
		memcpy((*i)->buffer, buffer->buffer, (*i)->width * (*i)->height * 4);
		
		glBindTexture(GL_TEXTURE_2D, (*i)->textureID);
		glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, (*i)->width, (*i)->height, 
						(*i)->bpp == 3 ? GL_RGB : GL_BGRA, GL_UNSIGNED_BYTE, 
						(*i)->buffer);
	}
}

int getWebKeyFromSDLKey(SDLKey key);

void handleSDLKeyEvent(Awesomium::WebView* webView, const SDL_Event& event)
{
	if(!(event.type == SDL_KEYDOWN || event.type == SDL_KEYUP))
		return;
	
	Awesomium::WebKeyboardEvent keyEvent;
	
	keyEvent.type = event.type == SDL_KEYDOWN? 
	Awesomium::WebKeyboardEvent::TYPE_KEY_DOWN : 
	Awesomium::WebKeyboardEvent::TYPE_KEY_UP;
	
	char* buf = new char[20];
	keyEvent.virtualKeyCode = getWebKeyFromSDLKey(event.key.keysym.sym);
	Awesomium::getKeyIdentifierFromVirtualKeyCode(keyEvent.virtualKeyCode, 
												  &buf);
	strcpy(keyEvent.keyIdentifier, buf);
	delete[] buf;
	
	keyEvent.modifiers = 0;
	
	if(event.key.keysym.mod & KMOD_LALT || event.key.keysym.mod & KMOD_RALT)
		keyEvent.modifiers |= Awesomium::WebKeyboardEvent::MOD_ALT_KEY;
	if(event.key.keysym.mod & KMOD_LCTRL || event.key.keysym.mod & KMOD_RCTRL)
		keyEvent.modifiers |= Awesomium::WebKeyboardEvent::MOD_CONTROL_KEY;
	if(event.key.keysym.mod & KMOD_LMETA || event.key.keysym.mod & KMOD_RMETA)
		keyEvent.modifiers |= Awesomium::WebKeyboardEvent::MOD_META_KEY;
	if(event.key.keysym.mod & KMOD_LSHIFT || event.key.keysym.mod & KMOD_RSHIFT)
		keyEvent.modifiers |= Awesomium::WebKeyboardEvent::MOD_SHIFT_KEY;
	if(event.key.keysym.mod & KMOD_NUM)
		keyEvent.modifiers |= Awesomium::WebKeyboardEvent::MOD_IS_KEYPAD;
	
	keyEvent.nativeKeyCode = event.key.keysym.scancode;
	
	if(event.type == SDL_KEYUP)
	{
		webView->injectKeyboardEvent(keyEvent);
	}
	else 
	{
		unsigned int chr;
		if((event.key.keysym.unicode & 0xFF80) == 0)
			chr = event.key.keysym.unicode & 0x7F;
		else
			chr = event.key.keysym.unicode;
		
		keyEvent.text[0] = chr;
		keyEvent.unmodifiedText[0] = chr;
		
		webView->injectKeyboardEvent(keyEvent);
		
		if(chr)
		{
			keyEvent.type = Awesomium::WebKeyboardEvent::TYPE_CHAR;
			keyEvent.virtualKeyCode = chr;
			keyEvent.nativeKeyCode = chr;
			webView->injectKeyboardEvent(keyEvent);
		}
	}
}

void Application::handleInput()
{
	SDL_Event event;
	
	while(SDL_PollEvent(&event))
	{
		switch(event.type)
		{
			case SDL_QUIT:
				shouldQuit = true;
				return;
			case SDL_MOUSEMOTION:
				if(isActiveWebTileFocused)
					webTiles[activeWebTile]->webView->
					injectMouseMove(event.motion.x, event.motion.y);
				
				if(isDragging)
					handleDragMove(event.motion.x, event.motion.y);
				break;
			case SDL_MOUSEBUTTONDOWN:
				if(isActiveWebTileFocused)
				{
					if(event.button.button == SDL_BUTTON_LEFT)
						webTiles[activeWebTile]->webView->
						injectMouseDown(Awesomium::LEFT_MOUSE_BTN);
					else if(event.button.button == SDL_BUTTON_WHEELUP)
						webTiles[activeWebTile]->webView->injectMouseWheel(25);
					else if(event.button.button == SDL_BUTTON_WHEELDOWN)
						webTiles[activeWebTile]->webView->injectMouseWheel(-25);
				}
				else
				{
					if(event.button.button == SDL_BUTTON_LEFT)
						handleDragBegin(event.button.x, event.button.y);
					
					if(event.button.button == SDL_BUTTON_WHEELUP)
						webTiles[activeWebTile]->webView->injectMouseWheel(25);
					else if(event.button.button == SDL_BUTTON_WHEELDOWN)
						webTiles[activeWebTile]->webView->injectMouseWheel(-25);
				}
				break;
			case SDL_MOUSEBUTTONUP:
				if(isActiveWebTileFocused)
				{
					if(event.button.button == SDL_BUTTON_LEFT)
						webTiles[activeWebTile]->webView->
						injectMouseUp(Awesomium::LEFT_MOUSE_BTN);
				}
				else
				{
					if(event.button.button == SDL_BUTTON_LEFT)
						handleDragEnd(event.button.x, event.button.y);
				}
				break;
			case SDL_KEYDOWN:
			{
				if(event.key.keysym.sym == SDLK_ESCAPE)
				{
					shouldQuit = true;
					return;
				}
				else if(event.key.keysym.sym == SDLK_BACKQUOTE)
				{
					if(zoomStart > 0)
						return;
					
					if(isActiveWebTileFocused)
					{
						double curTime = SDL_GetTicks() / 1000.0;
						zoomDirection = false;
						zoomStart = curTime;
						zoomEnd = curTime + ZOOMTIME;
						isActiveWebTileFocused = false;
					}
					else
					{
						double curTime = SDL_GetTicks() / 1000.0;
						zoomDirection = true;
						zoomStart = curTime;
						zoomEnd = curTime + ZOOMTIME;
					}
					
					return;
				}
				else if(event.key.keysym.mod & KMOD_CTRL)
				{
					if(event.key.keysym.sym == SDLK_LEFT)
					{
						webTiles[activeWebTile]->webView->goToHistoryOffset(-1);
						return;
					}
					else if(event.key.keysym.sym == SDLK_RIGHT)
					{
						webTiles[activeWebTile]->webView->goToHistoryOffset(1);
						return;
					}
				}
				else if(event.key.keysym.mod & KMOD_ALT && event.key.keysym.sym == SDLK_t)
				{
					webTiles[activeWebTile]->toggleTransparency();
				}
				else if(event.key.keysym.mod & KMOD_ALT && event.key.keysym.sym == SDLK_x)
				{
					if(webTiles.size() > 1)
					{
						for(std::vector<WebTile*>::iterator i = webTiles.begin(); i != 
							webTiles.end(); ++i)
						{
							if(*i == webTiles[activeWebTile])
							{
								(*i)->webView->destroy();
								webTiles.erase(i);
								break;
							}
						}
						
						if(activeWebTile > 0)
						{
							activeWebTile--;
							startOff = offset + 1;
							animateTo(activeWebTile);
						}
						else
						{
							startOff = offset - 1;
							animateTo(activeWebTile);
						}
						
						webTiles[activeWebTile]->webView->focus();
						webTiles[activeWebTile]->webView->resumeRendering();
						
						return;
					}
				}
				else if(event.key.keysym.mod & KMOD_ALT && event.key.keysym.sym == SDLK_g)
				{
					addWebTileWithURL("http://www.google.com", WIDTH, HEIGHT);
					
					animateTo(webTiles.size() - 1);
					
					return;
				}
				
				handleSDLKeyEvent(webTiles[activeWebTile]->webView, event);
				
				break;
			}
			case SDL_KEYUP:
			{
				handleSDLKeyEvent(webTiles[activeWebTile]->webView, event);
				
				break;
			}
			default:
				break;
		}
	}
}

void Application::handleDragBegin(int x, int y)
{
	isDragging = true;
	
	startPos = (x / (double)WIDTH) * 10 - 5;
	startOff = offset;
	
	isDragging = true;
	
	startTime = SDL_GetTicks() / 1000.0;
	lastPos = startPos;
	
	endAnimation();
}

void Application::handleDragMove(int x, int y)
{
	double pos = (x / (double)WIDTH) * 10 - 5;
	
	int max = webTiles.size()-1;
	
	offset = startOff + (startPos - pos);
	
	if (offset > max)
		offset = max;
	
	if (offset < 0)
		offset = 0;
	
	double time = SDL_GetTicks() / 1000.0;
	
	if (time - startTime > 0.2)
	{
		startTime = time;
		lastPos = pos;
	}
}

void Application::handleDragEnd(int x, int y)
{
	double pos = (x / (double)WIDTH) * 10 - 5;
	
	if (isDragging)
	{
		// Start animation to nearest
		startOff += (startPos - pos);
		offset = startOff;
		
		double time = SDL_GetTicks() / 1000.0;
		double speed = (lastPos - pos)/((time - startTime) + 0.00001);
		
		if(speed > MAXSPEED)
			speed = MAXSPEED;
		
		if(speed < -MAXSPEED)
			speed = -MAXSPEED;
		
		startAnimation(speed);
	}
	
	isDragging = false;
}

bool Application::isReadyToQuit() const
{
	return shouldQuit;
}

void Application::onBeginNavigation(Awesomium::WebView* caller, 
									const std::string& url, 
									const std::wstring& frameName)
{
	std::cout << "[WebViewListener::onBeginNavigation]\n\tURL: " << url << std::endl;
}

void Application::onBeginLoading(Awesomium::WebView* caller, 
								 const std::string& url, 
								 const std::wstring& frameName, 
								 int statusCode, const std::wstring& mimeType)
{
	std::cout << "[WebViewListener::onBeginLoading]\n\tURL: " << url << "\n\tSTATUS CODE: " << 
	statusCode << "\n\tMIME TYPE: ";
	std::wcout << mimeType << std::endl;
}

void Application::onFinishLoading(Awesomium::WebView* caller)
{
	std::cout << "[WebViewListener::onFinishLoading]" << std::endl;
	caller->requestScrollData(L"");
}

void Application::onCallback(Awesomium::WebView* caller, 
							 const std::wstring& objectName, 
							 const std::wstring& callbackName, 
							 const Awesomium::JSArguments& args)
{
}

void Application::onReceiveTitle(Awesomium::WebView* caller, 
								 const std::wstring& title, 
								 const std::wstring& frameName)
{
	std::wcout << L"[WebViewListener::onReceiveTitle]\n\tTITLE: " << title << std::endl;
}

void Application::onChangeTooltip(Awesomium::WebView* caller, 
								  const std::wstring& tooltip)
{
}

void Application::onChangeCursor(Awesomium::WebView* caller, 
								 Awesomium::CursorType cursor)
{
}

void Application::onChangeKeyboardFocus(Awesomium::WebView* caller,
										bool isFocused)
{
	std::cout << "[WebViewListener::onChangeKeyboardFocus]\n\tFOCUSED: " << isFocused << std::endl;
}

void Application::onChangeTargetURL(Awesomium::WebView* caller, 
									const std::string& url)
{
}

void Application::onOpenExternalLink(Awesomium::WebView* caller, 
									 const std::string& url, 
									 const std::wstring& source)
{
	addWebTileWithURL(url, WIDTH, HEIGHT);
	std::cout << "[WebViewListener::onOpenExternalLink]\n\tURL: " << url << std::endl;
	animateTo(webTiles.size() - 1);
}

void Application::onRequestDownload(Awesomium::WebView* caller,
									const std::string& url)
{
	std::cout << "[WebViewListener::onRequestDownload]\n\tURL: " << url << std::endl;
}

void Application::onWebViewCrashed(Awesomium::WebView* caller)
{
	std::cout << "[WebViewListener::onWebViewCrashed]" << std::endl;
}

void Application::onPluginCrashed(Awesomium::WebView* caller, 
								  const std::wstring& pluginName)
{
	std::cout << "[WebViewListener::onPluginCrashed]" << std::endl;
}

void Application::onRequestMove(Awesomium::WebView* caller, int x, int y)
{
}

void Application::onGetPageContents(Awesomium::WebView* caller, 
									const std::string& url, 
									const std::wstring& contents)
{
}

void Application::onDOMReady(Awesomium::WebView* caller)
{
	std::cout << "[WebViewListener::onDOMReady]" << std::endl;
}

void Application::onRequestFileChooser(Awesomium::WebView* caller,
									   bool selectMultipleFiles,
									   const std::wstring& title,
									   const std::wstring& defaultPath)
{
	std::cout << "[WebViewListener::onRequestFileChooser]" << std::endl;
}

void Application::onGetScrollData(Awesomium::WebView* caller,
								  int contentWidth,
								  int contentHeight,
								  int preferredWidth,
								  int scrollX,
								  int scrollY)
{
	std::cout << "[WebViewListener::onGetScrollData]\n\tPAGE WIDTH: " <<
	contentWidth << "\n\tPAGE HEIGHT: " << contentHeight << std::endl;
}

void Application::onJavascriptConsoleMessage(Awesomium::WebView* caller,
											const std::wstring& message,
											int lineNumber,
											const std::wstring& source)
{
	std::wcout << "[WebViewListener::onJavascriptConsoleMessage]\n\tMESSAGE: " <<
		message << L"\n\tLINE: " << lineNumber << L"\n\tSOURCE: " << source << std::endl;
}

void Application::onGetFindResults(Awesomium::WebView* caller,
                                      int requestID,
                                      int numMatches,
                                      const Awesomium::Rect& selection,
                                      int curMatch,
                                      bool finalUpdate)
{
}

void Application::onUpdateIME(Awesomium::WebView* caller,
                                 Awesomium::IMEState imeState,
                                 const Awesomium::Rect& caretRect)
{
}

void Application::onShowContextMenu(Awesomium::WebView* caller,
                                   int mouseX,
								   int mouseY,
								   Awesomium::MediaType type,
								   int mediaState,
								   const std::string& linkURL,
								   const std::string& srcURL,
								   const std::string& pageURL,
								   const std::string& frameURL,
								   const std::wstring& selectionText,
								   bool isEditable,
								   int editFlags)
{
}

void Application::onRequestLogin(Awesomium::WebView* caller,
                                   int requestID,
								   const std::string& requestURL,
								   bool isProxy,
								   const std::wstring& hostAndPort,
								   const std::wstring& scheme,
								   const std::wstring& realm)
{
}

void Application::onChangeHistory(Awesomium::WebView* caller,
									int backCount,
									int forwardCount)
{
}

void Application::onFinishResize(Awesomium::WebView* caller,
									int width,
									int height)
{
}

void Application::onShowJavascriptDialog(Awesomium::WebView* caller,
											int requestID,
											int dialogFlags,
											const std::wstring& message,
											const std::wstring& defaultPrompt,
											const std::string& frameURL)
{
}

#define mapKey(a, b) case SDLK_##a: return Awesomium::KeyCodes::AK_##b;

int getWebKeyFromSDLKey(SDLKey key)
{
	switch(key)
	{
			mapKey(BACKSPACE, BACK)
			mapKey(TAB, TAB)
			mapKey(CLEAR, CLEAR)
			mapKey(RETURN, RETURN)
			mapKey(PAUSE, PAUSE)
			mapKey(ESCAPE, ESCAPE)
			mapKey(SPACE, SPACE)
			mapKey(EXCLAIM, 1)
			mapKey(QUOTEDBL, 2)
			mapKey(HASH, 3)
			mapKey(DOLLAR, 4)
			mapKey(AMPERSAND, 7)
			mapKey(QUOTE, OEM_7)
			mapKey(LEFTPAREN, 9)
			mapKey(RIGHTPAREN, 0)
			mapKey(ASTERISK, 8)
			mapKey(PLUS, OEM_PLUS)
			mapKey(COMMA, OEM_COMMA)
			mapKey(MINUS, OEM_MINUS)
			mapKey(PERIOD, OEM_PERIOD)
			mapKey(SLASH, OEM_2)
			mapKey(0, 0)
			mapKey(1, 1)
			mapKey(2, 2)
			mapKey(3, 3)
			mapKey(4, 4)
			mapKey(5, 5)
			mapKey(6, 6)
			mapKey(7, 7)
			mapKey(8, 8)
			mapKey(9, 9)
			mapKey(COLON, OEM_1)
			mapKey(SEMICOLON, OEM_1)
			mapKey(LESS, OEM_COMMA)
			mapKey(EQUALS, OEM_PLUS)
			mapKey(GREATER, OEM_PERIOD)
			mapKey(QUESTION, OEM_2)
			mapKey(AT, 2)
			mapKey(LEFTBRACKET, OEM_4)
			mapKey(BACKSLASH, OEM_5)
			mapKey(RIGHTBRACKET, OEM_6)
			mapKey(CARET, 6)
			mapKey(UNDERSCORE, OEM_MINUS)
			mapKey(BACKQUOTE, OEM_3)
			mapKey(a, A)
			mapKey(b, B)
			mapKey(c, C)
			mapKey(d, D)
			mapKey(e, E)
			mapKey(f, F)
			mapKey(g, G)
			mapKey(h, H)
			mapKey(i, I)
			mapKey(j, J)
			mapKey(k, K)
			mapKey(l, L)
			mapKey(m, M)
			mapKey(n, N)
			mapKey(o, O)
			mapKey(p, P)
			mapKey(q, Q)
			mapKey(r, R)
			mapKey(s, S)
			mapKey(t, T)
			mapKey(u, U)
			mapKey(v, V)
			mapKey(w, W)
			mapKey(x, X)
			mapKey(y, Y)
			mapKey(z, Z)
			mapKey(DELETE, DELETE)
			mapKey(KP0, NUMPAD0)
			mapKey(KP1, NUMPAD1)
			mapKey(KP2, NUMPAD2)
			mapKey(KP3, NUMPAD3)
			mapKey(KP4, NUMPAD4)
			mapKey(KP5, NUMPAD5)
			mapKey(KP6, NUMPAD6)
			mapKey(KP7, NUMPAD7)
			mapKey(KP8, NUMPAD8)
			mapKey(KP9, NUMPAD9)
			mapKey(KP_PERIOD, DECIMAL)
			mapKey(KP_DIVIDE, DIVIDE)
			mapKey(KP_MULTIPLY, MULTIPLY)
			mapKey(KP_MINUS, SUBTRACT)
			mapKey(KP_PLUS, ADD)
			mapKey(KP_ENTER, SEPARATOR)
			mapKey(KP_EQUALS, UNKNOWN)
			mapKey(UP, UP)
			mapKey(DOWN, DOWN)
			mapKey(RIGHT, RIGHT)
			mapKey(LEFT, LEFT)
			mapKey(INSERT, INSERT)
			mapKey(HOME, HOME)
			mapKey(END, END)
			mapKey(PAGEUP, PRIOR)
			mapKey(PAGEDOWN, NEXT)
			mapKey(F1, F1)
			mapKey(F2, F2)
			mapKey(F3, F3)
			mapKey(F4, F4)
			mapKey(F5, F5)
			mapKey(F6, F6)
			mapKey(F7, F7)
			mapKey(F8, F8)
			mapKey(F9, F9)
			mapKey(F10, F10)
			mapKey(F11, F11)
			mapKey(F12, F12)
			mapKey(F13, F13)
			mapKey(F14, F14)
			mapKey(F15, F15)
			mapKey(NUMLOCK, NUMLOCK)
			mapKey(CAPSLOCK, CAPITAL)
			mapKey(SCROLLOCK, SCROLL)
			mapKey(RSHIFT, RSHIFT)
			mapKey(LSHIFT, LSHIFT)
			mapKey(RCTRL, RCONTROL)
			mapKey(LCTRL, LCONTROL)
			mapKey(RALT, RMENU)
			mapKey(LALT, LMENU)
			mapKey(RMETA, LWIN)
			mapKey(LMETA, RWIN)
			mapKey(LSUPER, LWIN)
			mapKey(RSUPER, RWIN)
			mapKey(MODE, MODECHANGE)
			mapKey(COMPOSE, ACCEPT)
			mapKey(HELP, HELP)
			mapKey(PRINT, SNAPSHOT)
			mapKey(SYSREQ, EXECUTE)
		default: return Awesomium::KeyCodes::AK_UNKNOWN;
	}
}
