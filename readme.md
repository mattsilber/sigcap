# sigcap

This library is intended to help capture signatures with an easily-configurable style.

![sigcap Sample](https://github.com/mattsilber/sigcap/raw/master/sigcap.gif)

### Installation

```groovy
repositories {
    jcenter()
}

dependencies {
    compile('com.guardanis:sigcap:1.0.0')
}
```

### Usage

The basic component is the *SignatureInputView* which actually handles the touch-to-draw events for the signature, as well as the drawing for the baseline (and x-marker). All the default styling values can be overriden at the resource-level, or at the implementation level with the styled attributes.

If you want to implement the undo action outside the default dialog, you'd have to manually call *SignatureInputView.undoLastSignaturePath()*.

Calling *SignatureInputView.saveSignature()* will attempt to save the drawing cache of the SignatureInputView into a File and return the result if it's successful. Please note, it is highly recommended you delete the signature file after you're done using it.

### SignatureDialogBuilder

This helper class is all you need to integrate sigcap (unless you really want to get fancy), with strings and colors easily overriden through the resources (same as the default resources mentioned above).

Here's an example of how to call the SignatureDialogBuilder, from the gif example above:

```java
new SignatureDialogBuilder()
            .show(this, new SignatureDialogBuilder.SignatureEventListener() {
                @Override
                public void onSignatureEntered(File savedFile) {
                    new ImageFileRequest<ImageView>(MainActivity.this, savedFile)
                            .setTargetView((ImageView) findViewById(R.id.test_image))
                            .setFadeTransition()
                            .execute(); // Just showing the image 
                }

                @Override
                public void onSignatureInputCanceled() {
                    Toast.makeText(MainActivity.this, "Signature input canceled", Toast.LENGTH_SHORT)
                            .show();
                }

                @Override
                public void onSignatureInputError(Throwable e) {
                    if(e instanceof NoSignatureException) // They clicked confirm without entering anything
                        doSomethingOnNoSignatureEntered();
                    else Toast.makeText(MainActivity.this, "Signature error", Toast.LENGTH_SHORT)
                            .show();
                }
            });
```
