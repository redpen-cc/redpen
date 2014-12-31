package cc.redpen.parser;

final public class LineOffset implements Comparable {
    public int lineNum;
    public int offset;

    public LineOffset(int lineNum, int offset) {
        this.lineNum = lineNum;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "LineOffset{" +
                "lineNum=" + lineNum +
                ", offset=" + offset +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LineOffset that = (LineOffset) o;

        if (lineNum != that.lineNum) return false;
        if (offset != that.offset) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = lineNum;
        result = 31 * result + offset;
        return result;
    }


    @Override
    public int compareTo(Object o) {
        LineOffset that = (LineOffset) o;
        if (this.lineNum != that.lineNum) {
            return this.lineNum - that.lineNum;
        }
        return this.offset - that.offset;
    }
}
