# Migrating from Version 1x to Version 2x

Replace instances of `SignatureDialogBuilder.show(Activity, SignatureDialogBuilder.SignatureEventListener)` with:

`SignatureDialogBuilder.showStatelessAlertDialog(Activity, SignatureEventListener)` 

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

And you're done!...

Or, you can implement one of the new, `DialogFragment`-friendly methods:

`SignatureDialogBuilder.showDialogFragment(FragmentManager)`
`SignatureDialogBuilder.showDialogFragment(FragmentManager, String)`
`SignatureDialogBuilder.showDialogFragment(FragmentManager, String, SignatureEventListener)` 

Or, include the `com.guardanis:sigcap-androidx:2+` library and implement using one of the `AppCompatDialogFragment`-friendly functions:

`SignatureDialogBuilder.showAppCompatDialogFragment(FragmentManager)`
`SignatureDialogBuilder.showAppCompatDialogFragment(FragmentManager, String)`
`SignatureDialogBuilder.showAppCompatDialogFragment(FragmentManager, String, SignatureEventListener)` 