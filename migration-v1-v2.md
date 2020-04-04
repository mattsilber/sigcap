# Migrating from Version 1x to Version 2x

Replace instances of `SignatureDialogBuilder.show(Activity, SignatureDialogBuilder.SignatureEventListener)` with:

`SignatureDialogBuilder.showStatelessAlertDialog(Activity, SignatureEventListener)` 

Done!...

Or, you can implement one of the new, `DialogFragment`-friendly methods:

`SignatureDialogBuilder.showDialogFragment(FragmentManager)`
`SignatureDialogBuilder.showDialogFragment(FragmentManager, String)`
`SignatureDialogBuilder.showDialogFragment(FragmentManager, String, SignatureEventListener)` 

Or, include the `com.guardanis:sigcap-androidx:2+` library and implement using one of the `AppCompatDialogFragment`-friendly functions:

`SignatureDialogBuilder.showAppCompatDialogFragment(FragmentManager)`
`SignatureDialogBuilder.showAppCompatDialogFragment(FragmentManager, String)`
`SignatureDialogBuilder.showAppCompatDialogFragment(FragmentManager, String, SignatureEventListener)` 