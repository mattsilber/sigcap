# sigcap

[![Download](https://img.shields.io/maven-central/v/com.guardanis/sigcap)](https://search.maven.org/artifact/com.guardanis/sigcap)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-sigcap-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/3331)
![Tests](https://github.com/mattsilber/sigcap/actions/workflows/test.yml/badge.svg)

This library is intended to help capture signatures with an easily-configurable style.

![sigcap Sample](https://github.com/mattsilber/sigcap/raw/master/sigcap.gif)

### Installation

```groovy
repositories {
    mavenCentral()
}

dependencies {
    compile('com.guardanis:sigcap:3+')
    compile('com.guardanis:sigcap-androidx:3+')
}
```

### Usage

The basic component is the `SignatureInputView` which delegates the touch/draw handling to respective `SignatureTouchController`/`SignatureRenderer` instances. All the default styling values can be overriden at the resource-level, at the implementation level with the styled attributes, or by applying a custom `SignatureRenderer` instance.

If you want to implement the undo action outside the default dialog, you'd have to manually call `SignatureInputView.undoLastSignaturePath()`.

Calling `SignatureInputView.saveSignature()` will render the signature `Path` instances into a `Bitmap` and return it wrapped inside a `SignatureResponse`. You can configure result rendering options (like baseline visibility) by supplying custom `SignatureRequest` options before saving.

If you want to save the response to a file, you can call `SignatureResponse.saveToFileCache` which will return a Future to the `File` the signature's Bitmap was stored in. Make sure to delete the signature file once you're done with it.

### In a dialog: `SignatureDialogBuilder`

This helper class is all you need to integrate `sigcap`, with text and colors easily overridden through the resources (same as the default resources mentioned above).

If all you want to do is show a Dialog and you don't care about orientation changes or state, just create a `SignatureEventListener` and pass it to your `SignatureDialogBuilder` instance:

```java
SignatureEventListener eventListener = new SignatureEventListener() {
    
    @Override
    public void onSignatureEntered(SignatureResponse response) {
        Bitmap signatureImage = response.getResult();
                                                           
        // Alternatively store the Bitmap response in a File
        File savedFile = response.saveToFileCache()
            .get();
    }
                                                   
    @Override
    public void onSignatureInputError(Throwable e) {
        if (e instanceof NoSignatureException) {
            // They clicked confirm without entering anything
        }
        else if (e instanceof CanceledSignatureInputException) {
            // They clicked cancel
        }
        else {
            Toast.makeText(MainActivity.this, "Signature error", Toast.LENGTH_SHORT)
                .show();
        }
     }                                                  
};

new SignatureDialogBuilder()
    .showStatelessAlertDialog(this, eventListener);
```

Or you can create a `SignatureDialogFragment`:

```java
String tag = "fragment_tag";

// Display the `SignatureDialogFragment`
new SignatureDialogBuilder()
    .showDialogFragment(this, tag, eventListener);

// Find the `SignatureDialogFragment` and set the `SignatureEventListener`
// If your `Activity` or calling `Fragment` does not implement `SignatureEventListener`, you
// will need to manually reset the `SignatureEventListener` when restoring.
SignatureDialogFragment fragment = (SignatureDialogFragment) getFragmentManager()
    .findFragmentByTag(tag);

if (fragment != null) {
    fragment.setSignatureEventListener(eventListener);
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
    ?.setSignatureEventListener(eventListener)

```

### In a custom layout

You can also include the `SignatureInputView` directly in your layout:

```xml
<com.guardanis.sigcap.SignatureInputView
    android:id="@+id/sig__input_view"
    android:layout_width="match_parent"
    android:layout_height="@dimen/sig__default_dialog_input_view_height"
    android:background="@color/sig__default_background"/>
```

#### Getting the drawn signature

First, check that there actually is some form of signature-input. If there is, save the signature to a `SignatureResponse` you can use:

```java
SignatureInputView sigInputView = (SignatureInputView) findViewById(R.id.sig__input_view);

if (sigInputView.isSignatureInputAvailable()) {
    SignatureResponse response = sigInputView.saveSignature();
    // Do something with the SignatureResponse...
    // Bitmap responseBitmap = response.getResult();
    // Future<File> = response.saveToFileCache();
}
```

#### Handling the undo-last-path action

The `SignatureInputView` does not contain the View from the dialogs that triggers the undo-last-path action. When using the `SignatureInputView` directly in your layout, you will instead need to implement a custom undo action that calls `SignatureInputView.undoLastSignaturePath()` yourself, if you desire that behavior:

```java
SignatureInputView sigInputView = (SignatureInputView) findViewById(R.id.sig__input_view);
sigInputView.undoLastSignaturePath();
```

### Migrating Version 2x to Version 3x

Read this [migration guide](https://github.com/mattsilber/sigcap/raw/master/migration-v2-v3.md).

### Migrating Version 1x to Version 2x

Read this [migration guide](https://github.com/mattsilber/sigcap/raw/master/migration-v1-v2.md).

### Error: "No variant found for 'sigcap'" when building the sample

Disable the experimental setting for `Only sync the active variant` in **Android Studio → Settings → Experimental**.