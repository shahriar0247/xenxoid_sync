from app import app
import os
from app.common_functions import get_temp

try:
    os.remove(get_temp())
except:
    pass

if __name__ == "__main__":
    app.run(debug=True, host="127.0.0.1", port=65023)