import hashlib
import string
from sawtooth_sdk.protobuf.transaction_pb2 import TransactionHeader
from sawtooth.sawtooth_sdk.processor.exceptions import InternalError, InvalidTransaction


class BallotTransactionHandler(TransactionHandler):
    def __init__(self, namespace_prefix):
        self._namespace_prefix = namespace_prefix

    @property
    def family_name(self):
        return 'ballot'

    @property
    def family_versions(self):
        return ['1.0']

    @property
    def namespaces(self):
        return [self._namespace_prefix]

    def apply(self, transaction, context):
        header = transaction.header
        signer = header.signer_public_key 

        Ballot_payload = BallotPayload.from_bytes(transaction.payload)

        xo_state = BallotState(context)

        if BallotPayload.action == 'create':
            return
        elif BallotPayload.action == 'vote':
            return
        elif BallotPayload.action == 'spoil':
            return
        else:
            raise InvalidTransaction('Unhandled action: {}'.format(
                BallotPayload.action))

## Class to encode the ballot
class BallotPayload:

    def __init__(self, payload):
        try:
            # The payload is csv utf-8 encoded string
            name, action, candidate = payload.decode().split(",")
        except ValueError:
            raise InvalidTransaction("Invalid payload serialization")

        if not name:
            raise InvalidTransaction('Name is required')

        if '|' in name:
            raise InvalidTransaction('Name cannot contain "|"')

        if not action:
            raise InvalidTransaction('Action is required')

        if action not in ('create', 'vote', 'spoil'):
            raise InvalidTransaction('Invalid action: {}'.format(action))

        if action == 'take':
            candidate = string(candidate)

        self._name = name
        self._action = action
        self._candidate = candidate

    @staticmethod
    def from_bytes(payload):
        return BallotPayload(payload=payload)

    @property
    def name(self):
        return self._name

    @property
    def action(self):
        return self._action

    @property
    def space(self):
        return self._candidate

## Holds the ballot
class Ballot:
    def __init__(self, name, candidate, state):
        self.name = name
        self.candidate = candidate
        self.state = state

## Defines the state of the ballot
class BallotState:

    TIMEOUT = 3

    def __init__(self, context):
        """Constructor.
        Args:
            context (sawtooth_sdk.processor.context.Context): Access to
                validator state from within the transaction processor.
        """

        self._context = context
        self._address_cache = {}

    def delete_game(self, ballot_name):
        """Delete the Ballot named ballot_name from state. May be used for spoiling
        Args:
            ballot_name (str): The name.
        Raises:
            KeyError: The Ballot with ballot_name does not exist.
        """

        ballots = self._load_ballots(ballot_name=ballot_name)

        del ballots[ballot_name]
        if ballots:
            self._store_ballot(ballot_name, ballots=ballots)
        else:
            self._delete_ballot(ballot_name)

    def set_game(self, ballot_name, ballot):
        """Store the ballot in the validator state.
        Args:
            ballot_name (str): The name.
            ballot (Ballot): The information specifying the current ballot.
        """

        ballots = self._load_ballots(ballot_name=ballot_name)

        ballots[ballot_name] = ballot

        self._store_ballot(ballot_name, ballots=ballots)

    def get_ballot(self, ballot_name):
        """Get the ballot associated with ballot_name.
        Args:
            ballot_name (str): The name.
        Returns:
            (Ballot): All the information specifying a ballot.
        """

        return self._load_ballots(ballot_name=ballot_name).get(ballot_name)
    
    @staticmethod
    def _make_ballot_address(ballot_name):
        return BALLOT_NAMESPACE + hashlib.sha512(ballot_name.encode('utf-8')).hexdigest()[:64]

    def _store_ballot(self, ballot_name, ballots):
        address = _make_ballot_address(ballot_name)

        state_data = self._serialize(ballots)

        self._address_cache[address] = state_data

        self._context.set_state(
            {address: state_data},
            timeout=self.TIMEOUT)

    def _delete_game(self, ballot_name):
        address = _make_ballot_address(ballot_name)

        self._context.delete_state(
            [address],
            timeout=self.TIMEOUT)

        self._address_cache[address] = None

    def _load_ballots(self, ballot_name):
        address = _make_ballot_address(ballot_name)

        if address in self._address_cache:
            if self._address_cache[address]:
                serialized_ballots = self._address_cache[address]
                ballots = self._deserialize(serialized_ballots)
            else:
                ballots = {}
        else:
            state_entries = self._context.get_state(
                [address],
                timeout=self.TIMEOUT)
            if state_entries:

                self._address_cache[address] = state_entries[0].data

                ballots = self._deserialize(data=state_entries[0].data)

            else:
                self._address_cache[address] = None
                ballots = {}

        return ballots

    def _deserialize(self, data):
        """Take bytes stored in state and deserialize them into Python
        Ballot objects.
        Args:
            data (bytes): The UTF-8 encoded string stored in state.
        Returns:
            (dict): ballot name (str) keys, Ballot values.
        """

        ballots = {}
        try:
            for ballot in data.decode().split("|"):
                name, candidate, state = ballot.split(",")

                ballots[name] = Ballot(name, candidate, state)
        except ValueError:
            raise InternalError("Failed to deserialize game data")

        return ballots

    def _serialize(self, ballots):
        """Takes a dict of game objects and serializes them into bytes.
        Args:
            games (dict): game name (str) keys, Game values.
        Returns:
            (bytes): The UTF-8 encoded string stored in state.
        """

        ballot_strs = []
        for name, b in ballots.items():
            ballot_str = ",".join(
                [name, b.candidate, b.state])
            ballot_strs.append(ballot_str)

        return "|".join(sorted(ballot_strs)).encode()