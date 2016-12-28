import theano
import theano.tensor as T
from theano.sandbox.rng_mrg import MRG_RandomStreams as RandomStreams
import numpy as np


def numpy_floatX(data):
    return np.asarray(data, dtype=theano.config.floatX)


class Dropout_layer(object):
    def __init__(self, x, p=0.5):
        use_noise = theano.shared(numpy_floatX(0.))
        trng = RandomStreams(415)

        self.output = T.switch(use_noise,
                               (x * trng.binomial(x.shape, p=p, n=1, dtype=x.dtype)),
                               x * p
                               )


class Embedding_layer(object):
    def __init__(self, x, emb, word_size=100, prefix='embedd_layer_'):
        self._init_params_(emb, prefix)

        n_steps = x.shape[1]
        n_samples = x.shape[0]

        self.x = T.transpose(x)

        self.output = self.emb[self.x.flatten()].reshape([n_steps, n_samples, word_size])

    def _init_params_(self, emb, prefix):
        # L2-normalize the embedding matrix
        emb_ = np.sqrt(np.sum(emb ** 2, axis=1))
        emb = emb / np.dot(emb_.reshape(-1, 1), np.ones((1, emb.shape[1])))
        emb[0, :] = 0.

        self.emb = theano.shared(
            value=np.asarray(emb, dtype=theano.config.floatX),
            name=prefix + 'emb',
            borrow=True
        )

        self.params = {prefix + 'emb': self.emb}


class Embedding_layer_uniEmb(object):
    def __init__(self, x, emb, word_size=100, prefix='embedd_layer_'):
        n_steps = x.shape[1]
        n_samples = x.shape[0]

        self.x = T.transpose(x)

        self.output = emb[self.x.flatten()].reshape([n_steps, n_samples, word_size])

        self.params = {}


class LogisticRegression(object):
    def __init__(self, x, y, in_size, out_size, prefix='lr_'):

        self._init_params_(in_size, out_size, prefix)

        self.y_given_x = T.nnet.softmax(T.dot(x, self.W) + self.b)

        self.y_d = T.argmax(self.y_given_x, axis=1)

        self.loss = -T.mean(T.log(self.y_given_x)[T.arange(y.shape[0]), y])

        self.error = T.mean(T.neq(self.y_d, y))

    def _init_params_(self, in_size, out_size, prefix):

        self.W = theano.shared(
            value=np.random.uniform(
                low=-np.sqrt(6. / (in_size + out_size)),
                high=np.sqrt(6. / (in_size + out_size)),
                size=(in_size, out_size)
            ).astype(theano.config.floatX),
            name=prefix+'W',
            borrow=True
        )

        self.b = theano.shared(
            value=np.random.uniform(
                low=-np.sqrt(6. / (in_size + out_size)),
                high=np.sqrt(6. / (in_size + out_size)),
                size=(out_size,)
            ).astype(theano.config.floatX),
            name=prefix+'b',
            borrow=True
        )

        self.params = {prefix + 'W': self.W, prefix + 'b': self.b}


class LSTM_layer(object):
    def __init__(self, x, mask=None, in_size=100, hidden_size=400, mean_pooling=False, prefix='lstm_'):
        """attention, every column in input is a sample"""
        self._init_params_(in_size, hidden_size, prefix)

        assert mask is not None

        n_steps = x.shape[0]
        if x.ndim == 3:
            n_samples = x.shape[1]
        else:
            n_samples = 1

        def _slice(_x, n, dim):
            if _x.ndim == 3:
                return _x[:, :, n * dim:(n + 1) * dim]
            return _x[:, n * dim:(n + 1) * dim]

        def _step(m_, x_, h_, c_):
            preact = T.dot(h_, self.U)
            preact += x_

            i = T.nnet.sigmoid(_slice(preact, 0, hidden_size))
            f = T.nnet.sigmoid(_slice(preact, 1, hidden_size))
            o = T.nnet.sigmoid(_slice(preact, 2, hidden_size))
            c = T.tanh(_slice(preact, 3, hidden_size))

            c = f * c_ + i * c
            c = m_[:, None] * c + (1. - m_)[:, None] * c_

            h = o * T.tanh(c)
            h = m_[:, None] * h + (1. - m_)[:, None] * h_

            return h, c

        input = (T.dot(x, self.W) + self.b)

        rval, updates = theano.scan(_step,
                                    sequences=[mask, input],
                                    outputs_info=[T.alloc(numpy_floatX(0.), n_samples, hidden_size),
                                                  T.alloc(numpy_floatX(0.), n_samples, hidden_size)],
                                    name=prefix+'_scan',
                                    n_steps=n_steps)

        if mean_pooling:
            hidden_sum = (rval[0] * mask[:, :, None]).sum(axis=0)
            self.output = hidden_sum / mask.sum(axis=0)[:, None]
        else:
            self.output = rval[0][-1, :, :]
            self.out_all = rval[0]

    def _init_params_(self, in_size, hidden_size, prefix):
        def random_weights(x_dim, y_dim):
            return np.random.uniform(
                low=-np.sqrt(6. / (x_dim + y_dim)),
                high=np.sqrt(6. / (x_dim + y_dim)),
                size=(x_dim, y_dim)
            ).astype(theano.config.floatX)

        self.W = theano.shared(
            value=np.concatenate(
                [random_weights(in_size, hidden_size),
                 random_weights(in_size, hidden_size),
                 random_weights(in_size, hidden_size),
                 random_weights(in_size, hidden_size)],
                axis=1
            ).astype(theano.config.floatX),
            name=prefix+'W',
            borrow=True
        )

        self.U = theano.shared(
            value=np.concatenate(
                [random_weights(hidden_size, hidden_size),
                 random_weights(hidden_size, hidden_size),
                 random_weights(hidden_size, hidden_size),
                 random_weights(hidden_size, hidden_size)],
                axis=1
            ).astype(theano.config.floatX),
            name=prefix+'U',
            borrow=True
        )

        self.b = theano.shared(
            value=np.zeros((4 * hidden_size,)).astype(theano.config.floatX),
            name=prefix+'b',
            borrow=True
        )

        self.params = {prefix + 'W': self.W, prefix + 'U': self.U, prefix + 'b': self.b}


class GRU_layer(object):
    def __init__(self, x, mask=None, in_size=100, hidden_size=400, mean_pooling=False, prefix='gru_'):
        """attention, every column in input is a sample"""
        self._init_params_(in_size, hidden_size, prefix)

        assert mask is not None

        n_steps = x.shape[0]
        if x.ndim == 3:
            n_samples = x.shape[1]
        else:
            n_samples = 1

        def _slice(_x, n, dim):
            if _x.ndim == 3:
                return _x[:, :, n * dim:(n + 1) * dim]
            return _x[:, n * dim:(n + 1) * dim]

        def _step(m_, x_, h_):
            preact = T.dot(h_, self.U)
            preact += x_

            z = T.nnet.sigmoid(_slice(preact, 0, hidden_size))
            r = T.nnet.sigmoid(_slice(preact, 1, hidden_size))
            c = T.tanh(_slice(preact, 2, hidden_size) * r + (T.ones_like(r) - r) * _slice(x_, 2, hidden_size))

            h = (T.ones_like(z) - z) * c + z * h_
            h = m_[:, None] * h + (1. - m_)[:, None] * h_

            return h

        input = (T.dot(x, self.W) + self.b)

        rval, updates = theano.scan(_step,
                                    sequences=[mask, input],
                                    outputs_info=[T.alloc(numpy_floatX(0.), n_samples, hidden_size)],
                                    name=prefix+'_scan',
                                    n_steps=n_steps)

        if mean_pooling:
            hidden_sum = (rval * mask[:, :, None]).sum(axis=0)
            self.output = hidden_sum / mask.sum(axis=0)[:, None]
        else:
            self.output = rval[-1, :, :]
            self.out_all = rval

    def _init_params_(self, in_size, hidden_size, prefix):
        def random_weights(x_dim, y_dim):
            return np.random.uniform(
                low=-np.sqrt(6. / (x_dim + y_dim)),
                high=np.sqrt(6. / (x_dim + y_dim)),
                size=(x_dim, y_dim)
            ).astype(theano.config.floatX)

        self.W = theano.shared(
            value=np.concatenate(
                [random_weights(in_size, hidden_size),
                 random_weights(in_size, hidden_size),
                 random_weights(in_size, hidden_size)],
                axis=1
            ).astype(theano.config.floatX),
            name=prefix + 'W',
            borrow=True
        )

        self.U = theano.shared(
            value=np.concatenate(
                [random_weights(hidden_size, hidden_size),
                 random_weights(hidden_size, hidden_size),
                 random_weights(hidden_size, hidden_size)],
                axis=1
            ).astype(theano.config.floatX),
            name=prefix + 'U',
            borrow=True
        )

        self.b = theano.shared(
            value=np.zeros((3 * hidden_size,)).astype(theano.config.floatX),
            name=prefix + 'b',
            borrow=True
        )

        self.params = {prefix+'W': self.W, prefix+'U': self.U, prefix+'b': self.b}


class GRU_layer_uniParam(object):
    def __init__(self, x, W, U, b, mask=None, in_size=100, hidden_size=400, mean_pooling=False, prefix='uni_gru_'):
        """attention, every column in input is a sample"""

        self.W = W
        self.U = U
        self.b = b

        assert mask is not None

        n_steps = x.shape[0]
        if x.ndim == 3:
            n_samples = x.shape[1]
        else:
            n_samples = 1

        def _slice(_x, n, dim):
            if _x.ndim == 3:
                return _x[:, :, n * dim:(n + 1) * dim]
            return _x[:, n * dim:(n + 1) * dim]

        def _step(m_, x_, h_):
            preact = T.dot(h_, self.U)
            preact += x_

            z = T.nnet.sigmoid(_slice(preact, 0, hidden_size))
            r = T.nnet.sigmoid(_slice(preact, 1, hidden_size))
            c = T.tanh(_slice(preact, 2, hidden_size) * r + (T.ones_like(r) - r) * _slice(x_, 2, hidden_size))

            h = (T.ones_like(z) - z) * c + z * h_
            h = m_[:, None] * h + (1. - m_)[:, None] * h_

            return h

        input = (T.dot(x, self.W) + self.b)

        rval, updates = theano.scan(_step,
                                    sequences=[mask, input],
                                    outputs_info=[T.alloc(numpy_floatX(0.), n_samples, hidden_size)],
                                    name=prefix+'_scan',
                                    n_steps=n_steps)

        if mean_pooling:
            hidden_sum = (rval * mask[:, :, None]).sum(axis=0)
            self.output = hidden_sum / mask.sum(axis=0)[:, None]
        else:
            self.output = rval[-1, :, :]
            self.out_all = rval


class DMN_GRU(object):
    def __init__(self, x, l, mask=None, hidden_size=400, mean_pooling=False, prefix='dmn_gru_'):
        """attention, every column in input is a sample"""
        self._init_params_(hidden_size, prefix)

        assert mask is not None

        n_steps = x.shape[0]
        if x.ndim == 3:
            n_samples = x.shape[1]
        else:
            n_samples = 1

        def _slice(_x, n, dim):
            if _x.ndim == 3:
                return _x[:, :, n * dim:(n + 1) * dim]
            return _x[:, n * dim:(n + 1) * dim]

        def _step(m_, x_, h_):
            preact = T.dot(h_, self.U)
            preact += x_

            _x = _slice(x_, 2, hidden_size)
            _z = T.concatenate([_x, l, _x * l, T.abs_(_x - l), T.dot(T.dot(T.transpose(x_), self.W0), l)], axis=1)
            g = T.nnet.sigmoid(T.dot(T.tanh(T.dot(_z, self.W1) + self.b1), self.W2) + self.b2)

            z = T.nnet.sigmoid(_slice(preact, 0, hidden_size))
            r = T.nnet.sigmoid(_slice(preact, 1, hidden_size))
            c = T.tanh(_slice(preact, 2, hidden_size) * r + (T.ones_like(r) - r) * _slice(x_, 2, hidden_size))

            _h = (T.ones_like(z) - z) * c + z * h_
            h = T.batched_dot(g, _h) + T.batched_dot(T.ones_like(g) - g, h_)
            h = m_[:, None] * h + (1. - m_)[:, None] * h_

            return h

        input = (T.dot(x, self.W) + self.b)

        rval, updates = theano.scan(_step,
                                    sequences=[mask, input],
                                    outputs_info=[T.alloc(numpy_floatX(0.), n_samples, hidden_size)],
                                    name=prefix+'_scan',
                                    n_steps=n_steps)

        if mean_pooling:
            hidden_sum = (rval * mask[:, :, None]).sum(axis=0)
            self.output = hidden_sum / mask.sum(axis=0)[:, None]
        else:
            self.output = rval[-1, :, :]
            self.out_all = rval

    def _init_params_(self, hidden_size, prefix):
        def random_weights(x_dim, y_dim):
            return np.random.uniform(
                low=-np.sqrt(6. / (x_dim + y_dim)),
                high=np.sqrt(6. / (x_dim + y_dim)),
                size=(x_dim, y_dim)
            ).astype(theano.config.floatX)

        self.W = theano.shared(
            value=np.concatenate(
                [random_weights(hidden_size, hidden_size),
                 random_weights(hidden_size, hidden_size),
                 random_weights(hidden_size, hidden_size)],
                axis=1
            ).astype(theano.config.floatX),
            name=prefix+'W',
            borrow=True
        )

        self.W0 = theano.shared(
            value=random_weights(hidden_size, hidden_size),
            name=prefix+'W0',
            borrow=True
        )

        self.W1 = theano.shared(
            value=random_weights(4 * hidden_size, hidden_size),
            name=prefix + 'W1',
            borrow=True
        )

        self.W2 = theano.shared(
            value=np.random.uniform(
                low=-np.sqrt(6. / hidden_size),
                high=np.sqrt(6. / hidden_size),
                size=(hidden_size, )
            ).astype(theano.config.floatX),
            name=prefix + 'W2',
            borrow=True
        )

        self.U = theano.shared(
            value=np.concatenate(
                [random_weights(hidden_size, hidden_size),
                 random_weights(hidden_size, hidden_size),
                 random_weights(hidden_size, hidden_size)],
                axis=1
            ).astype(theano.config.floatX),
            name=prefix+'U',
            borrow=True
        )

        self.b = theano.shared(
            value=np.zeros((3 * hidden_size,)).astype(theano.config.floatX),
            name=prefix+'b',
            borrow=True
        )

        self.b1 = theano.shared(
            value=np.zeros(
                shape=(hidden_size,),
                dtype=theano.config.floatX
            ),
            name=prefix + 'b1',
            borrow=True
        )

        self.b2 = theano.shared(
            value=numpy_floatX(0.),
            name=prefix + 'b2',
            borrow=True
        )

        self.params = {prefix + 'W': self.W,
                       prefix + 'U': self.U,
                       prefix + 'b': self.b,
                       # prefix + 'W0': self.W0,
                       prefix + 'W1': self.W1,
                       prefix + 'W2': self.W2,
                       prefix + 'b1': self.b1,
                       prefix + 'b2': self.b2}


class KBMN_GRU(object):
    def __init__(self, x, l, kbm, mask=None, hidden_size=400, mean_pooling=False, prefix='kbmn_gru_'):
        """attention, every column in input is a sample"""
        self._init_params(kbm, hidden_size, prefix=prefix)

        assert mask is not None

        n_steps = x.shape[0]
        if x.ndim == 3:
            n_samples = x.shape[1]
        else:
            n_samples = 1

        def _slice(_x, n, dim):
            if _x.ndim == 3:
                return _x[:, :, n * dim:(n + 1) * dim]
            return _x[:, n * dim:(n + 1) * dim]

        def _step(m_, x_, h_):
            preact = T.dot(h_, self.U)
            preact += x_

            _x = _slice(x_, 2, hidden_size)
            _k = T.dot(T.nnet.softmax(T.dot(_x, self.V)), kbm)
            _z = T.concatenate([_x, _k, l, _x * l, _k * l, _x * _k, T.abs_(_x - l), T.abs_(_k - l), T.abs_(_x - _k)], axis=1)  # T.dot(T.dot(T.transpose(x_), self.W0), l)
            g = T.nnet.sigmoid(T.dot(T.tanh(T.dot(_z, self.W1) + self.b1), self.W2) + self.b2)

            z = T.nnet.sigmoid(_slice(preact, 0, hidden_size))
            r = T.nnet.sigmoid(_slice(preact, 1, hidden_size))
            c = T.tanh(_slice(preact, 2, hidden_size) * r + (T.ones_like(r) - r) * _slice(x_, 2, hidden_size))

            _h = (T.ones_like(z) - z) * c + z * h_
            h = T.batched_dot(g, _h) + T.batched_dot(T.ones_like(g) - g, h_)
            h = m_[:, None] * h + (1. - m_)[:, None] * h_

            return h

        input = (T.dot(x, self.W) + self.b)

        rval, updates = theano.scan(_step,
                                    sequences=[mask, input],
                                    outputs_info=[T.alloc(numpy_floatX(0.), n_samples, hidden_size)],
                                    name=prefix+'_scan',
                                    n_steps=n_steps)

        if mean_pooling:
            hidden_sum = (rval * mask[:, :, None]).sum(axis=0)
            self.output = hidden_sum / mask.sum(axis=0)[:, None]
        else:
            self.output = rval[-1, :, :]
            self.out_all = rval

    def _init_params(self, kbm, hidden_size, prefix):

        def random_weights(x_dim, y_dim):
            return np.random.uniform(
                low=-np.sqrt(6. / (x_dim + y_dim)),
                high=np.sqrt(6. / (x_dim + y_dim)),
                size=(x_dim, y_dim)
            ).astype(theano.config.floatX)

        n_kbitems = 198

        self.V = theano.shared(
            value=random_weights(hidden_size, n_kbitems),
            name=prefix+'V',
            borrow=True
        )

        self.W = theano.shared(
            value=np.concatenate(
                [random_weights(hidden_size, hidden_size),
                 random_weights(hidden_size, hidden_size),
                 random_weights(hidden_size, hidden_size)],
                axis=1
            ).astype(theano.config.floatX),
            name=prefix + 'W',
            borrow=True
        )

        self.W0 = theano.shared(
            value=random_weights(hidden_size, hidden_size),
            name=prefix + 'W0',
            borrow=True
        )

        self.W1 = theano.shared(
            value=random_weights(7 * hidden_size, hidden_size),
            name=prefix + 'W1',
            borrow=True
        )

        self.W2 = theano.shared(
            value=np.random.uniform(
                low=-np.sqrt(6. / hidden_size),
                high=np.sqrt(6. / hidden_size),
                size=(hidden_size,)
            ).astype(theano.config.floatX),
            name=prefix + 'W2',
            borrow=True
        )

        self.U = theano.shared(
            value=np.concatenate(
                [random_weights(hidden_size, hidden_size),
                 random_weights(hidden_size, hidden_size),
                 random_weights(hidden_size, hidden_size)],
                axis=1
            ).astype(theano.config.floatX),
            name=prefix + 'U',
            borrow=True
        )

        self.b = theano.shared(
            value=np.zeros((3 * hidden_size,)).astype(theano.config.floatX),
            name=prefix + 'b',
            borrow=True
        )

        self.b1 = theano.shared(
            value=np.zeros(
                shape=(hidden_size,),
                dtype=theano.config.floatX
            ),
            name=prefix + 'b1',
            borrow=True
        )

        self.b2 = theano.shared(
            value=numpy_floatX(0.),
            name=prefix + 'b2',
            borrow=True
        )

        self.params = {prefix + 'W': self.W,
                       prefix + 'U': self.U,
                       prefix + 'b': self.b,
                       # prefix + 'W0': self.W0,
                       prefix + 'W1': self.W1,
                       prefix + 'W2': self.W2,
                       prefix + 'b1': self.b1,
                       prefix + 'b2': self.b2,
                       prefix + 'V': self.V}
