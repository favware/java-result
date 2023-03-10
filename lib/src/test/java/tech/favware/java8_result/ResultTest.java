package tech.favware.java8_result;

import org.junit.jupiter.api.Test;
import tech.favware.java8_result.util.RaiseException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

	@Test
	void itShouldCreateOkFromPopulatedOptional() throws Throwable {
		Result<String> t = Result.ofOptional(Optional.of("foobar"), new IllegalArgumentException("Missing Value"));
		assertTrue(t.isOk());
		assertEquals("foobar", t.unwrap());
	}

	@Test
	void itShouldCreateErrFromEmptyOptional() {
		Result<String> t = Result.ofOptional(Optional.empty(), new IllegalArgumentException("Missing Value"));

		assertThrows(IllegalArgumentException.class, t::unwrap);
	}

	@Test
	void itShouldBeOkOnOk() {
		Result<String> t = Result.from(() -> "hey");
		assertTrue(t.isOk());
	}

	@Test
	void itShouldHoldValueOnOk() throws Throwable {
		Result<String> t = Result.from(() -> "hey");
		assertEquals("hey", t.unwrap());
	}

	@Test
	void itShouldMapOnOk() throws Throwable {
		Result<String> t = Result.from(() -> "hey");
		Result<Integer> intT = t.map((x) -> 5);
		intT.unwrap();
		assertEquals(5, intT.unwrap().intValue());
	}

	@Test
	void itShouldFlatMapOnOk() throws Throwable {
		Result<String> t = Result.from(() -> "hey");
		Result<Integer> intT = t.flatMap((x) -> Result.from(() -> 5));
		intT.unwrap();
		assertEquals(5, intT.unwrap().intValue());
	}

	@Test
	void itShouldOrElseOnOk() {
		String t = Result.from(() -> "hey").orElse("jude");
		assertEquals("hey", t);
	}

	@Test
	void itShouldReturnValueWhenRecoveringOnOk() {
		String t = Result.from(() -> "hey").recover((e) -> "jude");
		assertEquals("hey", t);
	}

	@Test
	void itShouldReturnValueWhenRecoveringWithOnOk() throws Throwable {
		String t = Result.from(() -> "hey")
				.recoverWith((x) ->
						Result.from(() -> "Jude")
				).unwrap();
		assertEquals("hey", t);
	}

	@Test
	void itShouldOrElseResultOnOk() throws Throwable {
		Result<String> t = Result.from(() -> "hey").orElseResult(() -> "jude");

		assertEquals("hey", t.unwrap());
	}

	@Test
	void itShouldBeErrOnErr() {
		Result<String> t = Result.from(() -> {
			throw new Exception("e");
		});
		assertFalse(t.isOk());
	}

	@Test
	void itShouldThrowExceptionOnGetOfErr() {
		Result<String> t = Result.from(() -> {
			throw new IllegalArgumentException("e");
		});

		assertThrows(IllegalArgumentException.class, t::unwrap);
	}

	@Test
	void itShouldMapOnErr() {
		Result<String> t = Result.from(() -> {
			throw new Exception("e");
		}).map((x) -> "hey" + x);

		assertFalse(t.isOk());
	}

	@Test
	void itShouldFlatMapOnErr() {
		Result<String> t = Result.from(() -> {
			throw new Exception("e");
		}).flatMap((x) -> Result.from(() -> "hey"));

		assertFalse(t.isOk());
	}

	@Test
	void itShouldOrElseOnErr() {
		String t = Result.<String>from(() -> {
			throw new IllegalArgumentException("e");
		}).orElse("jude");

		assertEquals("jude", t);
	}

	@Test
	void itShouldOrElseResultOnErr() throws Throwable {
		Result<String> t = Result.<String>from(() -> {
			throw new IllegalArgumentException("e");
		}).orElseResult(() -> "jude");

		assertEquals("jude", t.unwrap());
	}

	@Test
	void itShouldGetAndThrowUncheckedException() {
		Result<String> t = Result.from(() -> {
			throw new Exception();
		});

		assertThrows(RuntimeException.class, t::unwrapUnchecked);
	}

	@Test
	void itShouldGetValue() {
		final String result = Result.from(() -> "test").unwrapUnchecked();

		assertEquals("test", result);
	}

	@Test
	void itShouldReturnRecoverValueWhenRecoveringOnErr() {
		String t = Result.from(() -> "hey")
				.<String>map((x) -> {
					throw new Exception("fail");
				})
				.recover((e) -> "jude");

		assertEquals("jude", t);
	}

	@Test
	void itShouldReturnValueWhenRecoveringWithOnErr() throws Throwable {
		String t = Result.<String>from(() -> {
					throw new Exception("oops");
				})
				.recoverWith((x) ->
						Result.from(() -> "Jude")
				).unwrap();

		assertEquals("Jude", t);
	}

	@Test
	void itShouldHandleComplexChaining() {
		assertDoesNotThrow(() -> {
			Result.from(() -> "1").flatMap((x) -> Result.from(() -> Integer.valueOf(x))).recoverWith((t) -> Result.ok(1));
		});
	}

	@Test
	void itShouldPassErrIfPredicateIsFalse() {
		Result<Object> t1 = Result.from(() -> {
			throw new RuntimeException();
		}).filter(o -> false);

		Result<Object> t2 = Result.from(() -> {
			throw new RuntimeException();
		}).filter(o -> true);

		assertFalse(t1.isOk());
		assertFalse(t2.isOk());
	}

	@Test
	void isShouldPassOkOnlyIfPredicateIsTrue() {
		Result<String> t1 = Result.from(() -> "yo mama").filter(s -> s.length() > 0);
		Result<String> t2 = Result.from(() -> "yo mama").filter(s -> false);

		assertTrue(t1.isOk());
		assertFalse(t2.isOk());
	}

	@Test
	void itShouldReturnEmptyOptionalIfErrOrNullOk() {
		Optional<String> opt1 = Result.<String>from(() -> {
			throw new IllegalArgumentException("Expected exception");
		}).toOptional();

		Optional<String> opt2 = Result.<String>from(() -> null).toOptional();

		assertFalse(opt1.isPresent());
		assertFalse(opt2.isPresent());
	}

	@Test
	void isShouldReturnResultValueWrappedInOptionalIfNonNullOk() {
		Optional<String> opt1 = Result.from(() -> "yo mama").toOptional();

		assertTrue(opt1.isPresent());
	}

	@Test
	void itShouldThrowExceptionFromResultConsumerOnOkIfOk() {
		Result<String> t = Result.from(() -> "hey");

		assertThrows(IllegalArgumentException.class, () -> t.onOk(s -> {
			throw new IllegalArgumentException("Should be thrown.");
		}));
	}

	@Test
	void itShouldNotThrowExceptionFromResultConsumerOnOkIfErr() {
		Result<String> t = Result.from(() -> {
			throw new IllegalArgumentException("Expected exception");
		});

		assertDoesNotThrow(() -> {
			t.onOk(s -> {
				throw new IllegalArgumentException("Should NOT be thrown.");
			});
		});
	}

	@Test
	void itShouldNotThrowExceptionFromResultConsumerOnErrIfOk() {
		Result<String> t = Result.from(() -> "hey");

		assertDoesNotThrow(() -> {
			t.onErr(s -> {
				throw new IllegalArgumentException("Should NOT be thrown.");
			});
		});
	}

	@Test
	void itShouldThrowExceptionFromResultConsumerOnErrIfErr() {
		Result<String> t = Result.from(() -> {
			throw new IllegalArgumentException("Expected exception");
		});

		assertThrows(IllegalArgumentException.class, () -> t.onErr(s -> {
			throw new IllegalArgumentException("Should be thrown.");
		}));
	}

	@Test
	void itShouldThrowNewExceptionWhenInvokingOrElseThrowOnErr() {
		Result<String> t = Result.from(() -> {
			throw new Exception("Oops");
		});

		assertThrows(IllegalArgumentException.class, () -> t.<IllegalArgumentException>orElseThrow(() -> {
			throw new IllegalArgumentException("Should be thrown.");
		}));
	}

	@Test
	void itShouldNotThrowNewExceptionWhenInvokingOrElseThrowOnOk() {
		Result<String> t = Result.from(() -> "Ok");

		String result = t.<IllegalArgumentException>orElseThrow(() -> {
			throw new IllegalArgumentException("Should be thrown.");
		});

		assertEquals("Ok", result);
	}

	@Test
	void itShouldGetAndThrowOriginalExceptionIfUnchecked() {
		Result<String> t = Result.from(() -> {
			throw new IllegalStateException();
		});

		assertThrows(IllegalStateException.class, t::unwrapUnchecked);
	}

	@Test
	void itShouldThrowCheckedExceptionOnRaiseOnErr() {
		Result<String> t = Result.from(() -> {
			throw new RaiseException("Oops");
		});

		assertThrows(RaiseException.class, () -> t.raise(RaiseException.class));
	}

	@Test
	void itShouldNotThrowCheckedExceptionOnRaiseOnOk() throws Throwable {
		Result<String> t = Result.ok("ok");
		t.raise(RaiseException.class);

		assertEquals("ok", t.unwrap());
	}
}
