# Graphing for PetriNetCRN

These notebooks use a lot of resources, they may use more memory than you have. 
Execute with caution.

You will need a `PetriNetCRN.jar` or symlink to it in this directory, look at
`SimulationOptions` in [crn_utils.py](./crn_utils.py) for more settings info.

By default, this will output sample CSVs into `./csvs/`.

Each notebook is for a different Approximate Majority CRN:

### Tri Molecular
```math
\begin{align*}
X + X + Y &{\quad \stackrel{1} \longrightarrow \quad} X + X + X \\
X + Y + Y &{\quad \stackrel{1} \longrightarrow \quad} Y + Y + Y 
\end{align*}
```

### Double B
```math
\begin{align*}
X + Y &{\quad \stackrel{1} \longrightarrow \quad} B + B \\
X + B &{\quad \stackrel{1} \longrightarrow \quad} X + X \\
Y + B &{\quad \stackrel{1} \longrightarrow \quad} Y + Y \\
\end{align*}
```

### Single B
```math
\begin{align*}
X + Y &{\quad \stackrel{\frac{1}{2}} \longrightarrow \quad} X + B \\
X + Y &{\quad \stackrel{\frac{1}{2}} \longrightarrow \quad} Y + B \\
X + B &{\quad \stackrel{1} \longrightarrow \quad} X + X \\
Y + B &{\quad \stackrel{1} \longrightarrow \quad} Y + Y \\
\end{align*}
```

### Heavy B
```math
\begin{align*}
X + Y &{\quad \stackrel{1} \longrightarrow \quad} B_2 \\
X + B_2 &{\quad \stackrel{1} \longrightarrow \quad} X + X + X \\
Y + B_2 &{\quad \stackrel{1} \longrightarrow \quad} Y + Y + Y \\
\end{align*}
```