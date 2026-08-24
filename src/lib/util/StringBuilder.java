package lib.util;

public class StringBuilder {
    char[] value;
    int count;

    public StringBuilder() {
        this.value = new char[0];
    }

    public StringBuilder append(String str) {
        if (str == null) {
            return this;
        }
        return this.append(str.toCharArray());
    }

    public StringBuilder append(char[] str) {
        if (str == null) {
            return this;
        }

        for (char c : str) {
            this.append(c);
        }
        return this;
    }

    public StringBuilder append(char ch) {
        int newCount = this.count + 1;
        ensureCapacity(newCount);

        this.value[count] = ch;
        this.count = newCount;

        return this;
    }

    public String build() {
        return new String(this.value,0,this.count);
    }

    private void ensureCapacity(int minCapacity) {
        int oldCapacity = value.length;
        if (minCapacity > oldCapacity) {
            int newCapacity = oldCapacity + (oldCapacity >> 1); //increase by 50%
            if (newCapacity - minCapacity < 0) {
                newCapacity = minCapacity;
            }
            char[] oldBuffer = value;
            value = new char[newCapacity];

            System.arraycopy(oldBuffer, 0, value, 0, count);
        }
    }

    @Override
    public String toString() {
        return this.build();
    }
}
