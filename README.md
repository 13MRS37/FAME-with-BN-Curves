# FAME with BN Curves – CP-ABE Implementation with JavaFX

A Java implementation of **FAME (Fast Attribute-based Message Encryption)**, a Ciphertext-Policy Attribute-Based Encryption (CP-ABE) scheme, extended with multiple elliptic-curve pairing configurations, a JavaFX graphical interface, and experimental performance evaluation.

The project explores the use of different pairing configurations based on **Barreto–Naehrig (BN) elliptic curves**, comparing Type-F configurations against the original Type-D configuration used by the initial FAME implementation.

---

## 1. Project Overview

FAME with BN Curves is an extension of the original [JPBC-FAME implementation](https://github.com/chrizchow/JPBC-FAME).

The project focuses on experimenting with different elliptic-curve pairing parameters and provides an interactive environment for testing the encryption and decryption process.

The main additions and features include:

- Integration of 16 Type-F pairing parameter configurations based on BN elliptic curves.
- Retention of the original Type-D pairing configuration as a reference baseline.
- JavaFX graphical user interface.
- Dynamic insertion and removal of user attributes.
- Selection of pairing configurations through `.properties` files.
- CP-ABE encryption and decryption of plaintext messages.
- Console-based performance measurements for the main FAME operations.
- Support for AND-only access policies containing up to 10 attributes.

The implementation is intended for academic research, experimentation, and educational purposes.

## 2. Cryptographic Background

### 2.1 Attribute-Based Encryption

Attribute-Based Encryption (ABE) is a public-key encryption paradigm in which access to encrypted data is determined by attributes rather than by the identity of an individual user.

This project implements **Ciphertext-Policy Attribute-Based Encryption (CP-ABE)**.

In CP-ABE, the encryptor defines an access policy associated with the ciphertext. A user can decrypt the ciphertext only if their secret key contains attributes that satisfy the specified policy.

### 2.2 FAME

FAME (Fast Attribute-based Message Encryption), proposed by Shashank Agrawal and Melissa Chase, is a CP-ABE construction designed to provide efficient attribute-based encryption.

The implementation follows the main FAME operations:

1. **Setup:** Generates the public parameters and master secret key.
2. **Key Generation (KeyGen):** Generates a user secret key associated with a given set of attributes.
3. **Encryption:** Encrypts a message under an access policy.
4. **Decryption:** Recovers the original message when the user's attributes satisfy the ciphertext policy.

### 2.3 Hybrid Encryption

The implementation combines FAME with AES-based symmetric encryption.

The encryption process is based on the following steps:

1. A random element is generated in the target pairing group.
2. The element is used as input for deriving an AES encryption key.
3. AES encrypts the plaintext.
4. FAME protects the intermediate secret through its pairing-based ciphertext components.
5. During decryption, FAME recovers the intermediate secret, which is subsequently used to decrypt the AES-encrypted payload.

The resulting ciphertext structure contains the access policy, pairing-group elements, and the AES-encrypted data.

## 3. Elliptic-Curve Pairing Configurations

An important part of this project is the experimentation with different elliptic-curve pairing configurations.

The implementation includes two categories of pairing parameters.

### 3.1 Original Type-D Configuration

The original FAME implementation uses a Type-D pairing configuration.

The Type-D configuration included in this project is based on the original `a.properties` file from the upstream JPBC-FAME repository:

https://github.com/chrizchow/JPBC-FAME/blob/master/a.properties

For the experimental comparison, this configuration is used as the reference baseline and is stored as `default.properties`.

The configuration has an embedding degree of:

\[
k=6
\]

### 3.2 Type-F BN Configurations

The project additionally includes 16 Type-F pairing configurations based on the Barreto–Naehrig (BN) family of elliptic curves.

The configurations are named according to the bit length of the prime field parameter \(q\):

| Configuration | Approximate bit length of \(q\) |
|---|---:|
| `158.properties` | 158 bits |
| `190.properties` | 190 bits |
| `222.properties` | 222 bits |
| `254.properties` | 254 bits |
| `286.properties` | 286 bits |
| `318.properties` | 318 bits |
| `350.properties` | 350 bits |
| `382.properties` | 382 bits |
| `414.properties` | 414 bits |
| `446.properties` | 446 bits |
| `478.properties` | 478 bits |
| `510.properties` | 510 bits |
| `542.properties` | 542 bits |
| `574.properties` | 574 bits |
| `606.properties` | 606 bits |
| `638.properties` | 638 bits |

The Type-F configurations use an embedding degree of:

\[
k=12
\]

The Type-F parameter format includes the following values:

- `q`: Prime defining the base field.
- `r`: Order of the elliptic-curve subgroup.
- `b`: Curve coefficient.
- `beta`: Quadratic non-residue used in the quadratic extension.
- `alpha0`, `alpha1`: Coefficients used in the sextic extension polynomial.

The BN curve parameters \(q\), \(r\), and \(b\) were obtained from the BN curve family reference and converted from hexadecimal to decimal representation for use in the JPBC parameter files. The remaining Type-F extension parameters were selected/constructed as part of the parameter-generation process.

The generation and verification of the Type-F extension parameter (\beta) were performed using Python scripts and the SymPy symbolic mathematics library.

Specifically, the following SymPy function was used:

from sympy.ntheory import legendre_symbol

The legendre_symbol() function was used to identify and verify quadratic non-residues modulo the prime (q).

A candidate value of (\beta) was accepted when it satisfied:

[
\left(\frac{\beta}{q}\right)=-1
]

Three Python scripts were developed for this parameter-generation process:

compute_beta.py: Searches for candidate values of (\beta) with a specified bit length and verifies their quadratic non-residue property.

legendre_symbol.py: Identifies candidate values of (\beta) using the Legendre symbol.

compute_beta_a0_a1.py: Includes an implementation of the Tonelli–Shanks algorithm and uses SymPy for quadratic-residue verification.

The scripts use Python's built-in arithmetic operations together with SymPy's number-theoretic functionality to assist in generating and verifying the required parameters.

The Type-F pairing parameter structure is documented in the PBC Library Manual.

### 3.3 Configuration Selection

Each pairing configuration is stored in a separate `.properties` file in the project root directory.

The application loads the selected configuration through JPBC's `PairingFactory`.

This allows the same FAME implementation to be executed using different pairing parameters without changing the core cryptographic operations.

**Note:** The configuration files represent experimental pairing parameter sets. Their security levels should not be inferred solely from the bit length of \(q\) or the embedding degree \(k\). A security assessment requires consideration of the complete pairing setting and the relevant discrete-logarithm problems.

## 4. Access Policies and MSP

The implementation represents access policies through a Monotone Span Program (MSP).

The current implementation uses predefined MSP matrices for AND-only access policies.

For example, a policy containing three attributes is represented as:

```text
Department:IT and Role:Administrator and Clearance:High
```

The corresponding attributes are supplied in the format:

```text
ATTRIBUTE:VALUE
```

The current implementation supports:

- At least one attribute.
- A maximum of 10 attributes.
- AND-only access policies.

OR operations and arbitrary Boolean access structures are not currently supported.

## 5. Graphical User Interface

The project includes a JavaFX graphical interface that allows users to interact with the FAME implementation without using the console.

### GUI Features

- Dynamically add and remove attribute fields.
- Enter attributes using the `ATTRIBUTE:VALUE` format.
- Select a pairing configuration from the available `.properties` files.
- Enter a plaintext message.
- Execute encryption and decryption.
- Display the Base64-encoded AES-encrypted payload.
- Display the decrypted plaintext.
- Switch between light and dark themes.
- Clear the input and output fields.

### Ciphertext Display

The GUI displays the Base64 representation of the `aesBuf` field of the FAME ciphertext.

The complete ciphertext data structure contains:

- `policy_str`: The access policy.
- `C_0`: Pairing-group ciphertext components.
- `C`: Attribute-dependent ciphertext components.
- `Cp`: Pairing-group component associated with the encapsulated message.
- `aesBuf`: AES-encrypted plaintext payload.

The current GUI does not serialize or display all pairing-group components. It displays only the AES-encrypted payload.

## 6. Performance Evaluation

The project includes a console-based benchmarking workflow for measuring the execution time of the main FAME operations.

The measured operations are:

- Setup
- Key Generation
- Encryption
- Decryption

The experimental evaluation presented in the associated Bachelor's thesis investigates the execution time of the implementation for:

- 16 Type-F BN pairing configurations.
- The original Type-D reference configuration.
- Access policies containing between 1 and 10 attributes.

The analysis considers the relationship between the selected pairing configuration, the number of attributes, and the execution time of the cryptographic operations.

The thesis also presents comparisons between individual parameter configurations and discusses differences observed in their execution times.

These results are historical experimental findings from the thesis. They are not automatically reproduced by launching the GUI.

## 7. Requirements

- JDK 22 or later.
- JavaFX SDK 24.0.1 (or a compatible JavaFX SDK).
- JPBC dependencies included in the `lib/` directory.
- Windows, Linux, or macOS.

JavaFX is not bundled with the project and must be installed separately.

## 8. Installation and Execution (Windows PowerShell)

The following instructions use Windows PowerShell and the Java compiler directly. No IDE is required.

### 8.1 Download the Project

Clone the repository:

```powershell
git clone https://github.com/13MRS37/FAME_Upgraded.git
```

Alternatively, download the repository as a ZIP archive from GitHub and extract it.

Navigate to the project directory:

```powershell
cd .\FAME_Upgraded\Final_2025
```

### 8.2 Configure JavaFX

Download and extract the JavaFX SDK.

Set the path to the JavaFX `lib` directory:

```powershell
$env:PATH_TO_FX = "C:\javafx-sdk-24.0.1\lib"
```

Replace the example path with the actual location of your JavaFX SDK.

Verify that Java and the Java compiler are available:

```powershell
java -version
javac -version
```

### 8.3 Compile the Project

Create the output directory:

```powershell
New-Item -ItemType Directory -Force -Path .\out
```

Collect all Java source files:

```powershell
$javaFiles = Get-ChildItem -Path .\src -Recurse -Filter *.java |
    ForEach-Object { $_.FullName }
```

Compile the project:

```powershell
javac --module-path "$env:PATH_TO_FX" `
      --add-modules javafx.controls,javafx.fxml `
      -cp "lib/*" `
      -d out `
      $javaFiles
```

### 8.4 Run the Graphical Application

Launch the JavaFX interface:

```powershell
java --module-path "$env:PATH_TO_FX" `
     --add-modules javafx.controls,javafx.fxml `
     -cp "out;src;lib/*" `
     gui.Main
```

### 8.5 Run the Console Application

To execute the console-based implementation:

```powershell
java -cp "out;lib/*" hk.chriz.Main
```

The console application requests:

1. The number of attributes.
2. Each attribute in `ATTRIBUTE:VALUE` format.
3. A plaintext message.
4. The name of the pairing parameter `.properties` file.

It then executes Setup, KeyGen, Encryption, and Decryption and displays the elapsed time for each operation.

**Important:** Execute the application from the `Final_2025` directory. The application searches for `.properties` files in its current working directory.

## 9. Security Considerations

This project is an academic implementation and is not intended for production use.

The current AES implementation uses:

```java
AES/ECB/PKCS5Padding
```

The AES key is derived from the byte representation of an intermediate pairing-group element using the implementation's existing key-generation procedure.

AES-ECB does not provide authenticated encryption and is not recommended for protecting sensitive production data. A production implementation would require a reviewed key-derivation mechanism and an authenticated encryption mode, such as AES-GCM.

The implementation has not undergone an independent cryptographic security audit.

## 10. References

1. **Ben Lynn.** *PBC Library Manual – Type F Internals.*  
   https://crypto.stanford.edu/pbc/manual/ch08s08.html

2. **P. S. L. M. Barreto and M. Naehrig.** *Pairing-Friendly Elliptic Curves of Prime Order.* Cryptology ePrint Archive, Report 2005/133, 2005.  
   https://eprint.iacr.org/2005/133.pdf

3. **G. C. C. F. Pereira, M. A. Simplício Jr., M. Naehrig, and P. S. L. M. Barreto.** *A Family of Implementation-Friendly BN Elliptic Curves.* Cryptology ePrint Archive, Report 2010/429, 2010.  
   https://eprint.iacr.org/2010/429.pdf

4. **Standard Curve Database.** *Barreto–Naehrig (BN) Curves.*  
   https://std.neuromancer.sk/bn

5. **S. Agrawal and M. Chase.** *FAME: Fast Attribute-based Message Encryption.* Proceedings of the 2017 ACM SIGSAC Conference on Computer and Communications Security (CCS 2017).

6. **C. D. de Caro and V. Iovino.** *jPBC: Java Pairing Based Cryptography.* Proceedings of the 2011 IEEE Symposium on Computers and Communications (ISCC 2011).

7. **JPBC-FAME.** Original implementation repository.  
   https://github.com/chrizchow/JPBC-FAME

## 11. Acknowledgements

This project builds upon the original JPBC-FAME implementation and extends it with additional pairing parameter configurations, a graphical interface, and experimental evaluation.

The work is associated with a Bachelor's thesis (2024) focused on Attribute-Based Encryption and the experimental evaluation of different elliptic-curve pairing configurations.

## 12. Disclaimer

This software is provided for academic, educational, and experimental purposes only.

It is not intended for production cryptographic deployments or the protection of sensitive information.
