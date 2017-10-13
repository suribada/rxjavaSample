package com.suribada.rxjavabook;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Noh.Jaechun on 2017. 3. 9..
 */
public class RxTest {

	@Test
	public void testCollectionMinus() {
		List<Integer> first = Arrays.asList(1, 2, 3);
		List<Integer> second = Arrays.asList(1, 4, 5);

		Observable<Integer> firstObservable = Observable.fromIterable(first);

		firstObservable.filter(firstItem -> !second.contains(firstItem)).forEach(System.out::println);
	}

	@Test
	public void testTake() {
		Observable.range(1, 5).takeUntil(x -> x == 3).forEach(System.out::println);
		Observable.range(1, 5).takeWhile(x -> x == 3).forEach(System.out::println);
	}

	@Test
	public void testObserver() {
		List<Integer> list = Arrays.asList(1, 2, 3, 4);
		Observer<Integer> observer = new Observer<Integer>() {

			@Override
			public void onSubscribe(Disposable d) {
				System.out.println("onSubscribe = " + d.getClass().getName());
			}

			@Override
			public void onNext(Integer value) {
				System.out.println("value=" + value);
			}

			@Override
			public void onError(Throwable e) {
				System.out.println("onErrror=" + e.toString());
			}

			@Override
			public void onComplete() {
				System.out.println("onComplete");
			}
		};
		Observable.fromIterable(list).subscribe(observer);
		Observable.fromIterable(list).filter(x -> x > 2).map(x -> x + 5).subscribe(observer);
		observer.onNext(77);
	}

}