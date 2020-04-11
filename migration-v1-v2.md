# Migrating from Version 1x to Version 2x

### SignatureDialogBuilder

Replace instances of  

`SignatureDialogBuilder.show(Activity, SignatureDialogBuilder.SignatureEventListener)`

With:

`SignatureDialogBuilder.showStatelessAlertDialog(Activity, SignatureEventListener)` 

### SignatureEventListener

`com.guardanis.sigcap.SignatureDialogBuilder.SignatureEventListener` has been renamed to `com.guardanis.sigcap.SignatureEventListener`

`SignatureEventListener.onSignatureInputCanceled()` has been removed. Replace overridden instances with a check for a `CanceledSignatureInputException` in `onSignatureInputError(Throwable)`. e.g.:

```java
@Override
public void onSignatureInputError(Throwable e) { 
    if (e instanceof CanceledSignatureInputException) {
        // They canceled the Dialog
    }    
    ...
}
```

`SignatureEventListener.onSignatureEntered(Bitmap)` method signature has changed to `SignatureEventListener.onSignatureEntered(SignatureResponse)`. The resulting `Bitmap` can be obtained by calling `SignatureResponse.getResult()`, and a file can be manually generated via `SignatureResponse.saveToFileCache(Context)`.

### Styleables

And replace overridden resource styleables of:

```
baseline_height
baseline_paddingHorizontal
baseline_paddingBottom
baseline_x_mark
baseline_x_mark_offsetVertical
```

With matching keys of:

```
baselineStrokeWidth
baselinePaddingHorizontal
baselinePaddingBottom
baselineXMarkLength
baselineXMarkOffsetVertical
```

### Resources

And replace overridden resource values of:

```
R.dimen.sig__default_signature_stroke
R.dimen.sig__default_baseline_height
R.dimen.sig__default_baseline_x_mark
```

With matching resources names of:

```
R.dimen.sig__default_signature_stroke_width
R.dimen.sig__default_baseline_stroke_width
R.dimen.sig__default_baseline_x_mark_length
```

### Addendum

You can now also implement one of the new, `DialogFragment`-friendly `SignatureDialogBuilder` methods:

`SignatureDialogBuilder.showDialogFragment(FragmentManager)`
`SignatureDialogBuilder.showDialogFragment(FragmentManager, String)`
`SignatureDialogBuilder.showDialogFragment(FragmentManager, String, SignatureEventListener)` 

Or, include the `com.guardanis:sigcap-androidx:2+` library and implement using one of the `AppCompatDialogFragment`-friendly functions:

`SignatureDialogBuilder.showAppCompatDialogFragment(FragmentManager)`
`SignatureDialogBuilder.showAppCompatDialogFragment(FragmentManager, String)`
`SignatureDialogBuilder.showAppCompatDialogFragment(FragmentManager, String, SignatureEventListener)` 