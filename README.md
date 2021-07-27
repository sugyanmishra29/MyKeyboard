# MyKeyboard
I have tried experimenting with several methods, classes that Android provides so as to
ensure that my program works correctly. Many bugs got introduced in the process.
So, I have commented out several code snippets in this file as an effort to minimise those issues.
I haven't eliminated such unnecessary code mostly because it could be used at a later stage
during the app's development process.

Yet this app still needs a lot of work behind the scenes and more optimisation is needed.

## LOGIC:
    1.  keyboard.xml file was created whose root element is FloatingKeyboardView.
    2.  qwerty.xml file contains info about the rows in the keyboard layout and the buttons present in it.
    3.  method.xml has info regarding input-method
    4.  Once the keyboard is built, we enclose this keyboard view inside a rectangle (object of class Rect)
    5.  There is a handle bar on top of this rectangle which is drawn using onDraw(canvas) method and class Path.
        The handle bar, for the purpose of this POC, is self determined.
            If this POC has to be included as a feature in any product,
            then it would be done as per the directions from the design team at that firm.

        This handle bar will be used to drag the keyboard view as per the user input -
            if the user drags the handle bar upwards, the keyboard will move upwards.
        User can drag the handle bar towards any direction and the keyboard will get dragged accordingly.

## ISSUES:
    1.  Unable to get this keyboard floating - this has to be done using WindowManager.addView()

## NEW ADDITIONS TO BE MADE:
    1.  Size controllers have to be implemented at the corners of the keyboard view so that the user can adjust the
        size of the keyboard according to his choice.
    2.  Keyboard Layout looks very primitive. Several UI changes are needed. This is just a prototype application
        built as a proof of concept only.
