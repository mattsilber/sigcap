# Migrating from Version 2.x to Version 3.x

Migrating from Version 1.x? You'll need this [migration guide](https://github.com/mattsilber/sigcap/raw/master/migration-v1-v2.md), too.

### Moved to MavenCentral

As of version 3.0.0, sigcap will be hosted on MavenCentral. Versions 2.x and below will remain on JCenter.

| Version 2.x | Version 3.x |
| :--- | :--- |
| `jcenter()` | `mavenCentral()` |

### FileCache -> SignatureFileManager

| Version 2.x | Version 3.x |
| :--- | :--- |
| `FileCache.clear(Context)` | `SignatureFileManager.deleteAll(Context)` |
| `FileCache(Context).getFile(String)` | `SignatureFileManager.createTempFile(Context)` |

Unlike `FileCache.clear(Context)`, which would attempt to synchronously delete files, `SignatureFileManager.deleteAll(Context)` will attempt to asynchronously delete files and returns a `Future<Boolean>` which will indicate if all files were actually deleted or not.