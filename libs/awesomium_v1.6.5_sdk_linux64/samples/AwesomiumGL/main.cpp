/**
 * This is a simple example of using Awesomium with OpenGL.
 *
 * It loads a page and displays it using OpenGL and GLUT.
 */

// Various included headers
#include <Awesomium/WebCore.h>
#if defined(__WIN32__) || defined(_WIN32)
#include <windows.h>
#include <gl/gl.h>
#include "glut.h"
#define GL_BGRA GL_BGRA_EXT
#elif defined(__APPLE__)
#include <unistd.h>
#include <OpenGL/OpenGL.h>
#include <GLUT/GLUT.h>
#elif defined (__linux__)
#include <unistd.h>
#include <string.h>
#include <GL/freeglut.h>
#include <GL/gl.h>
#endif

// Various macro definitions
#define WIDTH	512
#define HEIGHT	512
#define URL	"http://www.google.com"
#define UPDATE_DELAY_MS	25

void cleanup();
void display();
void update(int val);
void mouseMoved(int x, int y);
void mousePressed(int button, int state, int x, int y);
void keyPressed(unsigned char key, int x, int y);
void specialKeyPressed(int key, int x, int y);
void injectKey(int keyCode);

static Awesomium::WebView* webView = 0;
static Awesomium::WebCore* webCore = 0;

// Our main program
int main(int argc, char *argv[])
{
	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_RGB | GLUT_DOUBLE | GLUT_DEPTH);
	glutInitWindowSize(WIDTH, HEIGHT);
	glutCreateWindow("AwesomiumGL Sample");

	// Initialize OpenGL
	glViewport(0, 0, WIDTH, HEIGHT);
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    gluOrtho2D(0, WIDTH, 0, HEIGHT);

	// Create our WebCore singleton with the default options
	webCore = new Awesomium::WebCore();
	
	// Create a new WebView instance with a certain width and height, using the 
	// WebCore we just created
	webView = webCore->createWebView(WIDTH, HEIGHT);
	
	// Load a certain URL into our WebView instance
	webView->loadURL(URL);

	webView->focus();

	glutDisplayFunc(display);
	glutTimerFunc(UPDATE_DELAY_MS, update, 0);
	glutMouseFunc(mousePressed);
	glutMotionFunc(mouseMoved);
	glutPassiveMotionFunc(mouseMoved);
	glutKeyboardFunc(keyPressed);
	glutSpecialFunc(specialKeyPressed);

	atexit(cleanup);

	glutMainLoop();
	
	return 0;
}

void cleanup()
{
	// Destroy our WebView instance
	webView->destroy();
	
	// Destroy our WebCore instance
	// NOTE: Since we don't have access to the main loop of
	// our application (a limitation of GLUT), we scheduled
	// this cleanup() method using atexit. The problem is
	// that Awesomium schedules some items for cleanup using
	// atexit as well and their order of execution is not
	// guaranteed which may cause a crash to occur.
	//
	// The Solution: Don't use GLUT or atexit :-)
	delete webCore;
}

void display()
{
	glClear(GL_COLOR_BUFFER_BIT);

	// Flip image vertically
	glRasterPos2i(0, HEIGHT); 
	glPixelZoom(1.0f,-1.0f); 

	const Awesomium::RenderBuffer* renderBuffer = webView->render();

	if(renderBuffer)
	{
		// Draw pixels directly to screen from our image buffer
		glDrawPixels(WIDTH, HEIGHT, GL_BGRA, GL_UNSIGNED_BYTE, 
			renderBuffer->buffer);   
	}

	glutSwapBuffers();
}

void update(int val)
{
	webCore->update();

	// Call our display func when the WebView needs rendering
	if(webView->isDirty())
		glutPostRedisplay();

	glutTimerFunc(UPDATE_DELAY_MS, update, 0);
}

void mouseMoved(int x, int y)
{
	webView->injectMouseMove(x, y);
}

void mousePressed(int button, int state, int x, int y)
{
	if(button == GLUT_LEFT_BUTTON)
	{
		if(state == GLUT_DOWN)
			webView->injectMouseDown(Awesomium::LEFT_MOUSE_BTN);
		else
			webView->injectMouseUp(Awesomium::LEFT_MOUSE_BTN);
	}
}

void keyPressed(unsigned char key, int x, int y)
{
	if(key == 8 || key == 127) // Backspace or Delete key
	{
		injectKey(Awesomium::KeyCodes::AK_BACK);
		return;
	}
	else if(key == 9) // Tab key
	{
		injectKey(Awesomium::KeyCodes::AK_TAB);
		return;
	}
	else if(key == 27) // Escape key
	{
		exit(0);
	}
	
	Awesomium::WebKeyboardEvent keyEvent;
	keyEvent.text[0] = key;
	keyEvent.unmodifiedText[0] = key;
	keyEvent.type = Awesomium::WebKeyboardEvent::TYPE_CHAR;
	keyEvent.virtualKeyCode = key;
	keyEvent.nativeKeyCode = key;
	webView->injectKeyboardEvent(keyEvent);
}

void specialKeyPressed(int key, int x, int y)
{
	switch(key)
	{
	case GLUT_KEY_LEFT:
		injectKey(Awesomium::KeyCodes::AK_LEFT);
		break;
	case GLUT_KEY_UP:
		injectKey(Awesomium::KeyCodes::AK_UP);
		break;
	case GLUT_KEY_RIGHT:
		injectKey(Awesomium::KeyCodes::AK_RIGHT);
		break;
	case GLUT_KEY_DOWN:
		injectKey(Awesomium::KeyCodes::AK_DOWN);
		break;
	case GLUT_KEY_PAGE_UP:
		injectKey(Awesomium::KeyCodes::AK_PRIOR);
		break;
	case GLUT_KEY_PAGE_DOWN:
		injectKey(Awesomium::KeyCodes::AK_NEXT);
		break;
	case GLUT_KEY_HOME:
		injectKey(Awesomium::KeyCodes::AK_HOME);
		break;
	case GLUT_KEY_END:
		injectKey(Awesomium::KeyCodes::AK_END);
		break;
	}
}

void injectKey(int keyCode)
{
	Awesomium::WebKeyboardEvent keyEvent;

	char* buf = new char[20];
	keyEvent.virtualKeyCode = keyCode;
	Awesomium::getKeyIdentifierFromVirtualKeyCode(keyEvent.virtualKeyCode, 
												  &buf);
	strcpy(keyEvent.keyIdentifier, buf);
	delete[] buf;
	
	keyEvent.modifiers = 0;
	keyEvent.nativeKeyCode = 0;
	keyEvent.type = Awesomium::WebKeyboardEvent::TYPE_KEY_DOWN;

	webView->injectKeyboardEvent(keyEvent);

	keyEvent.type = Awesomium::WebKeyboardEvent::TYPE_KEY_UP;

	webView->injectKeyboardEvent(keyEvent);
}
