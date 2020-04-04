# sigcap

[![Download](https://api.bintray.com/packages/mattsilber/maven/sigcap/images/download.svg) ](https://bintray.com/mattsilber/maven/sigcap/_latestVersion)

This library is intended to help capture signatures with an easily-configurable style.

![sigcap Sample](https://github.com/mattsilber/sigcap/raw/master/sigcap.gif)

### Installation

```groovy
repositories {
    jcenter()
}

dependencies {
    compile('com.guardanis:sigcap:2+')
    compile('com.guardanis:sigcap-androidx:2+')
}
```

### Usage

The basic component is the `SignatureInputView` which delegates the touch/draw handling to respective `SignatureTouchController`/`SignatureRenderer` instances. All the default styling values can be overriden at the resource-level, at the implementation level with the styled attributes, or by applying a custom `SignatureRenderer` instance.

If you want to implement the undo action outside the default dialog, you'd have to manually call `SignatureInputView.undoLastSignaturePath()`.

Calling `SignatureInputView.saveSignature()` will render the signature `Path` instances into a `Bitmap` and return it wrapped inside a `SignatureResponse`. You can configure result rendering options (like baseline visibility) by supplying custom `SignatureRequest` options before saving.

If you want to save the response to a file, you can call `SignatureResponse.saveToFileCache` which will return a Future to the `File` the signature's Bitmap was stored in. Make sure to delete the signature file once you're done with it.

### SignatureDialogBuilder

This helper class is all you need to integrate `sigcap`, with text and colors easily overridden through the resources (same as the default resources mentioned above).

If all you want to do is show a Dialog and you don't care about orientation changes or state, just create a `SignatureEventListener` and pass it to your `SignatureDialogBuilder` instance:

```java
SignatureEventListener eventListsner = new SignatureEventListener() {
    
    @Override
    public void onSignatureEntered(SignatureResponse response) {
        Bitmap signatureImage = response.getResult();
                                                           
        // Alternatively store the Bitmap response in a File
        File savedFile = response.saveToFileCache()
            .get();
    }
                                                   
    @Override
    public void onSignatureInputCanceled() {
        Toast.makeText(MainActivity.this, "Signature input canceled", Toast.LENGTH_SHORT)
            .show();
    }
                                                   
    @Override
    public void onSignatureInputError(Throwable e) {
        if (e instanceof NoSignatureException) {
            // They clicked confirm without entering anything
        }
        else {
            Toast.makeText(MainActivity.this, "Signature error", Toast.LENGTH_SHORT)
                .show();
        }
     }                                                  
};

new SignatureDialogBuilder()
    .showStatelessAlertDialog(this, eventListsner);
```

Or you can create a `SignatureDialogFragment`:

```java
String tag = "fragment_tag";

// Display the `SignatureDialogFragment`
new SignatureDialogBuilder()
    .showDialogFragment(this, tag, eventListsner);

// Find the `SignatureDialogFragment` and set the `SignatureEventListener`
// If your `Activity` or calling `Fragment` does not implement `SignatureEventListener`, you
// will need to manually reset the `SignatureEventListener` when restoring.
SignatureDialogFragment fragment = (SignatureDialogFragment) getFragmentManager()
    .findFragmentByTag(tag);

if (fragment != null) {
    fragment.setSignatureEventListener(eventListsner);
}
```

Or, using the `sigcap-androidx` components, you can create an `AppCompatSignatureDialogFragment`:

```kotlin
// Display the `AppCompatSignatureDialogFragment`
SignatureDialogBuilder()
    .showAppCompatDialogFragment(supportFragmentManager, tag, eventListener)
    
// Find the `AppCompatSignatureDialogFragment` and set the `SignatureEventListener`
// If your `Activity` or calling `Fragment` does not implement `SignatureEventListener`, you
// will need to manually reset the `SignatureEventListener` when restoring.
supportFragmentManager.findAppCompatSignatureDialogFragment(tag)
    ?.setSignatureEventListener(eventListsner)

```