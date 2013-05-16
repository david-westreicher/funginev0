/**
 * This is a simple "Hello World!" example of using Awesomium.
 *
 * It loads a page, renders it once, and saves it to a file.
 *
 * Procedure:
 * -- Create the WebCore singleton
 * -- Create a new WebView and request for it to load a URL.
 * -- Wait for the WebView to finish loading.
 * -- Render the loaded page to a buffer.
 * -- Save the buffer to 'result.jpg'.
 * -- Clean up.
 */

// Various included headers
#include <Awesomium/WebCore.h>
#include <iostream>
#if defined(__WIN32__) || defined(_WIN32)
#include <windows.h>
#elif defined(__APPLE__)
#include <unistd.h>
#endif

// Various macro definitions
#define WIDTH	512
#define HEIGHT	512
#define URL	"http://www.google.com"

// Forward declaration of our update function
void update(int sleepMs);

// Our main program
int main()
{
	// Disable scrollbars via the WebCoreConfig
	Awesomium::WebCoreConfig config;
	config.setCustomCSS("::-webkit-scrollbar { display: none; }");

	// Create the WebCore singleton with our custom config
	Awesomium::WebCore* webCore = new Awesomium::WebCore(config);
	
	// Create a new WebView instance with a certain width and height, using the 
	// WebCore we just created
	Awesomium::WebView* webView = webCore->createWebView(WIDTH, HEIGHT);
	
	// Load a certain URL into our WebView instance
	webView->loadURL(URL);
	
	std::cout << "Page is now loading..." << std::endl;;
	
	// Wait for our WebView to finish loading
	while(webView->isLoadingPage())
		update(50);

	std::cout << "Page has finished loading." << std::endl;

	// Update once more a little longer to allow scripts and plugins
	// to finish loading on the page.
	update(300);
	
	// Get our rendered buffer from our WebView. All actual rendering takes 
	// place in our WebView sub-process which passes the rendered data to our 
	// main process during each call to WebCore::update.
	const Awesomium::RenderBuffer* renderBuffer = webView->render();
	
	// Make sure our render buffer is not NULL-- WebView::render will return
	// NULL if the WebView process has crashed.
	if(renderBuffer != NULL)
	{
		// Save our RenderBuffer directly to a JPEG image
		renderBuffer->saveToJPEG(L"./result.jpg");
		
		std::cout << "Saved a render of the page to 'result.jpg'." << std::endl;

		// Open up the saved JPEG
#if defined(__WIN32__) || defined(_WIN32)
		system("start result.jpg");
#elif defined(__APPLE__)
		system("open result.jpg");
#endif
	}
	
	// Destroy our WebView instance
	webView->destroy();

	// Update once more before we shutdown for good measure
	update(100);
	
	// Destroy our WebCore instance
	delete webCore;
	
	return 0;
}

void update(int sleepMs)
{
	// Sleep a specified amount
#if defined(__WIN32__) || defined(_WIN32)
	Sleep(sleepMs);
#elif defined(__APPLE__)
	usleep(sleepMs * 1000);
#endif

	// You must call WebCore::update periodically
	// during the lifetime of your application.
	Awesomium::WebCore::Get().update();
}