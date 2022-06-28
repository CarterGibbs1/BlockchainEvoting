import sys
sys.path.append('./sawtooth/sawtooth_sdk/processor/')
import TransactionProcessor
sys.path.append('./sawtooth/sawtooth_ballot/processor/')
from handler import BallotTransactionHandler

def main():
    # In docker, the url would be the validator's container name with
    # port 4004
    processor = TransactionProcessor(url='tcp://127.0.0.1:4004')

    handler = BallotTransactionHandler()

    processor.add_handler(handler)

    processor.start()
