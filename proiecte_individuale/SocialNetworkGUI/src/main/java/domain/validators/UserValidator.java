package socialnetwork.domain.validators;

import socialnetwork.domain.User;

public class UserValidator implements Validator<User> {
    /*
        @param: entity- User object
        @return: -
        @throws ValidationException daca nu avem un utilizator valid, adica are una din urmatoarele probleme:
            nume, prenume reprezentate de string vid
            nume, prenume din prea putine caractere pentru a reprezenta ceva real
            nume, prenume in care pe langa litere apar si alte caractere(de exemlu, caractere speciale sau cifre)
            id-ul corespunzator exista deja
     */
    @Override
    public void validate(User entity) throws ValidationException {
        String errors="";
        if(entity.getId() == null)
            errors+="Id can't be null";

        if(entity.getFirstName().equals(""))
            errors+="First name can't be empty\n";
        if(entity.getLastName().equals(""))
            errors+="Last name can't be empty\n";

        boolean allLetters=entity.getFirstName().chars().allMatch(Character::isLetter);
        if(!allLetters)
            errors+="First name can contains only letters\n";
        allLetters=entity.getLastName().chars().allMatch(Character::isLetter);
        if(!allLetters)
            errors+="Last name can contains only letters\n";

        if(entity.getFirstName().length() < 3)
            errors+="First name must be at least three characters long\n";
        if(entity.getLastName().length() < 3)
            errors+="Last name must be at least three characters long\n";

        if(!entity.getEmail().contains("@"))
            errors+="Email address should have the format: abc@yahoo.com or abc@gmail.com\n";
        if(!entity.getEmail().contains("."))
            errors+="Email address should have the format: abc@yahoo.com or abc@gmail.com\n";
        if(!entity.getEmail().contains("yahoo") && !entity.getEmail().contains("gmail"))
            errors+="Email address should have the format: abc@yahoo.com or abc@gmail.com\n";

        if(entity.getPassword().length()<3)
            errors+="Password must be at least three characters long\n";

        if(!errors.equals(""))
            throw new ValidationException(errors);
    }
}
