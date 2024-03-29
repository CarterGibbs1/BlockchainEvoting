# Copyright 2016, 2017 Intel Corporation
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ------------------------------------------------------------------------------

import binascii
import warnings

import secp256k1

from sawtooth_signing.core import SigningError
from sawtooth_signing.core import ParseError

from sawtooth_signing.core import PrivateKey
from sawtooth_signing.core import PublicKey
from sawtooth_signing.core import Context

class Secp256k1PrivateKey(PrivateKey):
    def __init__(self, secp256k1_private_key):
        self._private_key = secp256k1_private_key

    def get_algorithm_name(self):
        return "secp256k1"

    def as_hex(self):
        return binascii.hexlify(self.as_bytes()).decode()

    def as_bytes(self):
        return bytes(self._private_key.private_key)

    @property
    def secp256k1_private_key(self):
        return self._private_key

    @staticmethod
    def from_bytes(byte_str):
        return Secp256k1PrivateKey(secp256k1.PrivateKey(byte_str))

    @staticmethod
    def from_hex(hex_str):
        try:
            return Secp256k1PrivateKey.from_bytes(binascii.unhexlify(hex_str))
        except Exception as e:
            raise ParseError('Unable to parse hex private key: {}'.format(
                e)) from e

    @staticmethod
    def new_random():
        return Secp256k1PrivateKey(secp256k1.PrivateKey())


class Secp256k1PublicKey(PublicKey):
    def __init__(self, secp256k1_public_key):
        self._public_key = secp256k1_public_key

    @property
    def secp256k1_public_key(self):
        return self._public_key

    def get_algorithm_name(self):
        return "secp256k1"

    def as_hex(self):
        return binascii.hexlify(self.as_bytes()).decode()

    def as_bytes(self):
        with warnings.catch_warnings():  # squelch secp256k1 warning
            warnings.simplefilter('ignore')
            return self._public_key.serialize()

    @staticmethod
    def from_bytes(byte_str):
        public_key = secp256k1.PublicKey(byte_str, raw=True)
        return Secp256k1PublicKey(public_key)

    @staticmethod
    def from_hex(hex_str):
        try:
            return Secp256k1PublicKey.from_bytes(binascii.unhexlify(hex_str))
        except Exception as e:
            raise ParseError('Unable to parse hex public key: {}'.format(
                e)) from e


class Secp256k1Context(Context):
    def __init__(self):
        self._ctx = __CTX__

    def get_algorithm_name(self):
        return "secp256k1"

    def sign(self, message, private_key):
        try:
            signature = private_key.secp256k1_private_key.ecdsa_sign(message)
            signature = private_key.secp256k1_private_key \
                .ecdsa_serialize_compact(signature)

            return signature.hex()
        except Exception as e:
            raise SigningError('Unable to sign message: {}'.format(
                str(e))) from e

    def verify(self, signature, message, public_key):
        try:
            if isinstance(signature, str):
                signature = bytes.fromhex(signature)

            sig = public_key.secp256k1_public_key.ecdsa_deserialize_compact(
                signature)
            return public_key.secp256k1_public_key.ecdsa_verify(message, sig)
        # pylint: disable=broad-except
        except Exception:
            return False

    def new_random_private_key(self):
        return Secp256k1PrivateKey.new_random()

    def get_public_key(self, private_key):
        return Secp256k1PublicKey(private_key.secp256k1_private_key.pubkey)
