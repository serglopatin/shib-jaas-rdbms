import sys, base64
from passlib.context import CryptContext

def f1():
        if len(sys.argv)!=3:
                print "ERROR args"
                return 0

	myctx = CryptContext(schemes=["pbkdf2_sha256", "pbkdf2_sha512"])

        pw = base64.b64decode(sys.argv[1])
        hash = base64.b64decode(sys.argv[2])
	
	if not myctx.identify(hash):
		return 2

	return 1 if myctx.verify(pw, hash) else 0

if __name__=="__main__":
        print f1()

