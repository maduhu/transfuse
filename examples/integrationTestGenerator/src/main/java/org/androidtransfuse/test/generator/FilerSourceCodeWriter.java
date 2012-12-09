package org.androidtransfuse.test.generator;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;

public class FilerSourceCodeWriter extends CodeWriter {

    private final Filer filer;
    private final Collection<OutputStream> openStreams = new HashSet<OutputStream>();

    public FilerSourceCodeWriter(Filer filer) {
        this.filer = filer;
    }

    @Override
    public OutputStream openBinary(JPackage jPackage, String fileName) throws IOException {
        //generate a source file based on package and filename
        JavaFileObject sourceFile = filer.createSourceFile(toQualifiedClassName(jPackage, fileName));

        OutputStream os = sourceFile.openOutputStream();
        openStreams.add(os);

        return os;
    }

    private String toQualifiedClassName(JPackage pkg, String fileName) {
        return pkg.name() + "." + fileName.replace(".java", "");
    }

    @Override
    public void close() throws IOException {
        for (OutputStream openStream : openStreams) {
            openStream.flush();
            openStream.close();
        }
    }
}