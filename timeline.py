import matplotlib
matplotlib.use('Agg')

import matplotlib.pyplot as plt
import numpy as np
import matplotlib.ticker as ticker

x = np.arange(1960, 1999, 1)
years = dict.fromkeys(x, 0);

with open("years") as f:
    lines = f.read().splitlines()

for line in lines:
    year = int(line)
    years[year] += 1

plt.figure(1, figsize=(8, 2))
plt.xlim(1960, 1999)
ax = plt.axes()
ax.xaxis.set_tick_params(length=5)
ax.get_xaxis().set_minor_locator(ticker.MultipleLocator(1))
ax.get_yaxis().set_major_locator(ticker.MultipleLocator(1))
plt.stem(years.keys(), years.values())

plt.savefig("timeline")
