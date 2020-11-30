/*
 * A simple backup mod for Fabric
 * Copyright (C) 2020  Szum123321
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.szum123321.textile_backup.core.create.compressors;

import net.szum123321.textile_backup.Statics;
import net.szum123321.textile_backup.core.Utilities;
import net.szum123321.textile_backup.core.create.BackupContext;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.time.LocalDateTime;

public class ZipCompressor extends AbstractCompressor {
    public static ZipCompressor getInstance() {
        return new ZipCompressor();
    }

    @Override
    protected OutputStream createArchiveOutputStream(OutputStream stream, BackupContext ctx, int coreLimit) {
        ZipArchiveOutputStream arc =  new ZipArchiveOutputStream(stream);

        arc.setMethod(ZipArchiveOutputStream.DEFLATED);
        arc.setUseZip64(Zip64Mode.AsNeeded);
        arc.setLevel(Statics.CONFIG.compression);
        arc.setComment("Created on: " + Utilities.getDateTimeFormatter().format(LocalDateTime.now()));

        return arc;
    }

    @Override
    protected void addEntry(File file, String entryName, OutputStream arc) throws IOException {

        try (FileInputStream fileInputStream = new FileInputStream(file)){
            ZipArchiveEntry entry = (ZipArchiveEntry)((ZipArchiveOutputStream)arc).createArchiveEntry(file, entryName);

            if(isDotDat(file.getName()))
                entry.setMethod(ZipArchiveOutputStream.STORED);

            ((ZipArchiveOutputStream)arc).putArchiveEntry(entry);

            IOUtils.copy(fileInputStream, arc);

            ((ZipArchiveOutputStream)arc).closeArchiveEntry();
        }
    }

    protected static boolean isDotDat(String filename) {
        String[] arr = filename.split("\\.");
        return arr[arr.length - 1].contains("dat"); //includes dat_old
    }
}