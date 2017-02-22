/*
 * Copyright (C) 2017 greenrobot/ObjectBox (http://greenrobot.org)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.objectbox.rx;

import java.util.List;

import io.objectbox.query.Query;
import io.objectbox.reactive.*;
import io.reactivex.*;
import io.reactivex.functions.*;

/**
 * Static methods to Rx-ify ObjectBox queries.
 */
public abstract class RxQuery {
    /**
     * The returned Flowable emits Query results one by one. Once all results have been processed, onComplete is called.
     * Uses BackpressureStrategy.BUFFER.
     */
    public static <T> Flowable<T> flowableOneByOne(final Query<T> query) {
        return single(query).flattenAsFlowable(ListIdentity.<T>instance());
    }

    static enum ListIdentity implements Function<List<?>, List<?>> {
        INSTANCE;
        
        @SuppressWarnings({ "unchecked", "rawtypes" })
        static <T> Function<List<T>, List<T>> instance() {
            return (Function)INSTANCE;
        }
        
        @Override
        public List<?> apply(List<?> t) throws Exception {
            return t;
        }
    }

    
    /**
     * The returned Flowable emits Query results one by one. Once all results have been processed, onComplete is called.
     * Uses given BackpressureStrategy.
     */
    // FIXME I'd say there is no need for this overload since flowableOneByOne supports full backpressure out of box.
    public static <T> Flowable<T> flowableOneByOne(final Query<T> query, BackpressureStrategy strategy) {
        Flowable<T> f = flowableOneByOne(query);
        switch (strategy) {
        case DROP:
            return f.onBackpressureDrop();
        case LATEST:
            return f.onBackpressureLatest();
        default:
            return f;
        }
    }

    /**
     * The returned Observable emits Query results as Lists.
     * Never completes, so you will get updates when underlying data changes.
     */
    public static <T> Observable<List<T>> observable(final Query<T> query) {
        return Observable.create(new ObservableOnSubscribe<List<T>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<T>> emitter) throws Exception {
                final DataSubscription dataSubscription = query.subscribe().observer(new DataObserver<List<T>>() {
                    @Override
                    public void onData(List<T> data) {
                        if (!emitter.isDisposed()) {
                            emitter.onNext(data);
                        }
                    }
                });
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        dataSubscription.cancel();
                    }
                });
            }
        });
    }

    /**
     * The returned Single emits one Query result as a List.
     */
    public static <T> Single<List<T>> single(final Query<T> query) {
        return Single.create(new SingleOnSubscribe<List<T>>() {
            @Override
            public void subscribe(final SingleEmitter<List<T>> emitter) throws Exception {
                final DataSubscription dataSubscription = query.subscribe().single().observer(new DataObserver<List<T>>() {
                    @Override
                    public void onData(List<T> data) {
                        if (!emitter.isDisposed()) {
                            emitter.onSuccess(data);
                        }
                    }
                });
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        dataSubscription.cancel();
                    }
                });
            }
        });
    }
}
