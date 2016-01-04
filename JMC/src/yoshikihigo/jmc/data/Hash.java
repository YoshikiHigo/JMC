package yoshikihigo.jmc.data;

import java.util.Arrays;

public class Hash {

	final public byte[] value;

	public Hash(final byte[] value) {
		this.value = Arrays.copyOf(value, value.length);
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof Hash)) {
			return false;
		}
		final Hash target = (Hash) o;
		return Arrays.equals(this.value, target.value);
	}
}
