package org.pipeman.pipe_dl.pipe_file;

import org.pipeman.pipe_dl.util.misc.Utils;

public class PipePath {
    private long[] path;

    public PipePath(long[] path) {
        this.path = path;
    }

    public PipePath(String path) {
        this.path = Utils.parseToLongArray(path.split("\\."));
    }

    public String stringPath() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < path.length; i++) {
            builder.append(path[i]);
            if (i < path.length - 1) builder.append('.');
        }
        return builder.toString();
    }

    public PipePath addChild(long id) {
        long[] newPath = new long[path.length + 1];
        System.arraycopy(path, 0, newPath, 0, path.length);
        newPath[newPath.length - 1] = id;
        path = newPath;
        return this;
    }

    public long idAt(int depth) {
        return path[Math.max(0, path.length - 1 - depth)];
    }

    public long parent() {
        return idAt(1);
    }

    public long[] path() {
        return path;
    }

    public PipePath setPath(long[] newPath) {
        this.path = newPath;
        return this;
    }

    public String getNamedPath() {
        return "";
    }

    @Override
    public String toString() {
        return stringPath();
    }
}
