package com.guardanis.sigcap;

import com.guardanis.sigcap.SignatureFileManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

@RunWith(AndroidJUnit4.class)
public class SignatureFileManagerTests {

    @Test
    public void testDeleteAllFilesReturnsFalseForNullArray() {
        File[] files = null;

        assertFalse(SignatureFileManager.deleteAll(files));
    }

    @Test
    public void testDeleteAllFilesReturnsFalseForFailingDelete() {
        File badDelete = mock(File.class);
        File goodDelete = mock(File.class);

        Mockito.when(badDelete.delete())
                .thenReturn(false);

        Mockito.when(goodDelete.delete())
                .thenReturn(true);

        File[] files = new File[]{ goodDelete, goodDelete, badDelete };

        assertFalse(SignatureFileManager.deleteAll(files));
    }

    @Test
    public void testDeleteAllFilesReturnsTrueWhenDeleteSuccess() {
        File goodDelete = mock(File.class);

        Mockito.when(goodDelete.delete())
                .thenReturn(true);

        File[] files = new File[]{ goodDelete, goodDelete, goodDelete };

        assert(SignatureFileManager.deleteAll(files));
    }
}
