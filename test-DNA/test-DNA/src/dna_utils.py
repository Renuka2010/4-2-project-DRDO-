# src/dna_utils.py

def extract_mutations(dna_seq: str):
    """
    Extract 5 mutations from DNA sequence based on given positions.
    dna_seq must be at least 300 bases long.
    Returns dict {m1..m5}.
    """
    mutations = {"m1": 0, "m2": 0, "m3": 0, "m4": 0, "m5": 0}

    # M1: pos 50–51 & 80–81 (AG deleted)
    if dna_seq[49:51] != "AG" or dna_seq[79:81] != "AG":
        mutations["m1"] = 1

    # M2: pos 100–102 & 130–132 (CTT deleted)
    if dna_seq[99:102] != "CTT" or dna_seq[129:132] != "CTT":
        mutations["m2"] = 1

    # M3: pos 150–152 & 170–172 (GAG → GTG)
    if dna_seq[149:152] == "GTG" or dna_seq[169:172] == "GTG":
        mutations["m3"] = 1

    # M4: pos 200–202 & 220–222 (TGC → CGC)
    if dna_seq[199:202] == "CGC" or dna_seq[219:222] == "CGC":
        mutations["m4"] = 1

    # M5: pos 250 & 270 (C → T)
    if dna_seq[249] == "T" or dna_seq[269] == "T":
        mutations["m5"] = 1

    return mutations


def highlight_mutations(dna_seq: str, mutations: dict) -> str:
    """
    Return DNA sequence as HTML with colored highlights for mutations.
    Colors:
      m1 -> red, m2 -> orange, m3 -> purple, m4 -> blue, m5 -> green
    """
    seq = list(dna_seq)  # work with mutable list

    # Apply coloring if mutation detected
    if mutations.get("m1"):
        seq[49:51] = [f"<span style='color:red'>{dna_seq[49:51]}</span>"]
        seq[79:81] = [f"<span style='color:red'>{dna_seq[79:81]}</span>"]

    if mutations.get("m2"):
        seq[99:102] = [f"<span style='color:red'>{dna_seq[99:102]}</span>"]
        seq[129:132] = [f"<span style='color:red'>{dna_seq[129:132]}</span>"]

    if mutations.get("m3"):
        seq[149:152] = [f"<span style='color:red'>{dna_seq[149:152]}</span>"]
        seq[169:172] = [f"<span style='color:red'>{dna_seq[169:172]}</span>"]

    if mutations.get("m4"):
        seq[199:202] = [f"<span style='color:red'>{dna_seq[199:202]}</span>"]
        seq[219:222] = [f"<span style='color:red'>{dna_seq[219:222]}</span>"]

    if mutations.get("m5"):
        seq[249] = f"<span style='color:red'>{dna_seq[249]}</span>"
        seq[269] = f"<span style='color:red'>{dna_seq[269]}</span>"

    return "".join(str(x) for x in seq)
