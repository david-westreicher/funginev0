#ifndef __APPLICATION_H__
#define __APPLICATION_H__

#include "SDL.h"
#include "SDL_opengl.h"
#include <Awesomium/WebCore.h>
#include <vector>

// Some constants that configure certain aspects of the animation:
#define SPREADIMAGE         0.1     // The amount of spread between WebTiles
#define FLANKSPREAD         0.4     // How much a WebTile moves way from center
#define FRICTION            10.0    // Friction while "flowing" through WebTiles
#define MAXSPEED            7.0     // Throttle maximum speed to this value
#define ZOOMTIME            0.3     // Speed to zoom in/out of a WebTile
#define TRANSPARENT         0       // Whether or not we should use transparency

// Forward declaration, actually declared in WebTile.h
struct WebTile;

// Our main Application class is responsible for setting up the WebCore, the
// OpenGL scene, handling input, animating "WebTiles", and all other logic.
class Application : public Awesomium::WebViewListener
{
public:
	Application();
	~Application();
	
	void addWebTileWithURL(const std::string& url, int width, int height);
	
	void addWebTileWithHTML(const std::string& html, int width, int height);
	
	void update();
	
	void draw();
	
	void drawTile(int index, double off, double zoom);
	
	void updateWebTiles();
	
	void updateAnimationAtTime(double elapsed);
	
	void endAnimation();
	
	void driveAnimation();
	
	void startAnimation(double speed);

	void animateTo(int index);
	
	void handleInput();
	
	void handleDragBegin(int x, int y);
	
	void handleDragMove(int x, int y);
	
	void handleDragEnd(int x, int y);
	
	bool isReadyToQuit() const;
	
	// ** The following methods are inherited from WebViewListener:

	virtual void onBeginNavigation(Awesomium::WebView* caller, 
								   const std::string& url, 
								   const std::wstring& frameName);
	
	virtual void onBeginLoading(Awesomium::WebView* caller, 
								const std::string& url, 
								const std::wstring& frameName, 
								int statusCode, const std::wstring& mimeType);
	
	virtual void onFinishLoading(Awesomium::WebView* caller);
	
	virtual void onCallback(Awesomium::WebView* caller, 
							const std::wstring& objectName, 
							const std::wstring& callbackName, 
							const Awesomium::JSArguments& args);
	
	virtual void onReceiveTitle(Awesomium::WebView* caller, 
								const std::wstring& title, 
								const std::wstring& frameName);
	
	virtual void onChangeTooltip(Awesomium::WebView* caller, 
								 const std::wstring& tooltip);
	
	virtual void onChangeCursor(Awesomium::WebView* caller, 
								Awesomium::CursorType cursor);

	virtual void onChangeKeyboardFocus(Awesomium::WebView* caller,
									   bool isFocused);
	
	virtual void onChangeTargetURL(Awesomium::WebView* caller, 
								   const std::string& url);
	
	virtual void onOpenExternalLink(Awesomium::WebView* caller, 
									const std::string& url, 
									const std::wstring& source);

	virtual void onRequestDownload(Awesomium::WebView* caller,
								   const std::string& url);
	
	virtual void onWebViewCrashed(Awesomium::WebView* caller);
	
	virtual void onPluginCrashed(Awesomium::WebView* caller, 
								 const std::wstring& pluginName);
	
	virtual void onRequestMove(Awesomium::WebView* caller, int x, int y);
	
	virtual void onGetPageContents(Awesomium::WebView* caller, 
								   const std::string& url, 
								   const std::wstring& contents);
	
	virtual void onDOMReady(Awesomium::WebView* caller);

	virtual void onRequestFileChooser(Awesomium::WebView* caller,
										  bool selectMultipleFiles,
										  const std::wstring& title,
										  const std::wstring& defaultPath);

	virtual void onGetScrollData(Awesomium::WebView* caller,
								 int contentWidth,
								 int contentHeight,
								 int preferredWidth,
								 int scrollX,
								 int scrollY);

	virtual void onJavascriptConsoleMessage(Awesomium::WebView* caller,
											const std::wstring& message,
											int lineNumber,
											const std::wstring& source);

	virtual void onGetFindResults(Awesomium::WebView* caller,
                                      int requestID,
                                      int numMatches,
                                      const Awesomium::Rect& selection,
                                      int curMatch,
                                      bool finalUpdate);

	virtual void onUpdateIME(Awesomium::WebView* caller,
                                 Awesomium::IMEState imeState,
                                 const Awesomium::Rect& caretRect);

	virtual void onShowContextMenu(Awesomium::WebView* caller,
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
								   int editFlags);

	virtual void onRequestLogin(Awesomium::WebView* caller,
                                   int requestID,
								   const std::string& requestURL,
								   bool isProxy,
								   const std::wstring& hostAndPort,
								   const std::wstring& scheme,
								   const std::wstring& realm);

	virtual void onChangeHistory(Awesomium::WebView* caller,
									int backCount,
									int forwardCount);

	virtual void onFinishResize(Awesomium::WebView* caller,
									int width,
									int height);

	virtual void onShowJavascriptDialog(Awesomium::WebView* caller,
											int requestID,
											int dialogFlags,
											const std::wstring& message,
											const std::wstring& defaultPrompt,
											const std::string& frameURL);
	
	
protected:
	bool shouldQuit, isAnimating, isDragging, isActiveWebTileFocused, 
		zoomDirection;
	double offset, startTime, startOff, startPos, startSpeed, runDelta, lastPos,
		zoomStart, zoomEnd;
	int numTiles;
	std::vector<WebTile*> webTiles;
	GLfloat customColor[16];
	int activeWebTile;
	Awesomium::WebCore* webCore;
	int WIDTH, HEIGHT;
};

#endif