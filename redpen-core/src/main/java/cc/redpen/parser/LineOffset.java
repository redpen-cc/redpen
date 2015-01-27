/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.redpen.parser;

public final class LineOffset implements Comparable {
    public final int lineNum;
    public final int offset;

    /**
     * Constructor.
     *
     * @param lineNum line number
     * @param offset character offset position from start of line
     */
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
