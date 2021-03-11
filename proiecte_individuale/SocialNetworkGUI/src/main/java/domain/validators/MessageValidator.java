package socialnetwork.domain.validators;

import socialnetwork.domain.Message;
import socialnetwork.domain.User;

public class MessageValidator  implements Validator<Message>{
    /**
     *
     * @param entity
     * @throws ValidationException if Message object is invalid
     */
    @Override
    public void validate(Message entity) throws ValidationException {
        String errors="";

        //validari User
        if(entity.getFrom().getId() == null)
            errors+="The message must have a sender\n";
        if(entity.getFrom().getFirstName().equals(""))
            errors+="First name can't be empty\n";
        if(entity.getFrom().getLastName().equals(""))
            errors+="Last name can't be empty\n";
        boolean allLetters=entity.getFrom().getFirstName().chars().allMatch(Character::isLetter);
        if(!allLetters)
            errors+="First name can contains only letters\n";
        allLetters=entity.getFrom().getLastName().chars().allMatch(Character::isLetter);
        if(!allLetters)
            errors+="Last name can contains only letters\n";
        if(entity.getFrom().getFirstName().length() < 3)
            errors+="First name must be at least three characters long\n";
        if(entity.getFrom().getLastName().length() < 3)
            errors+="Last name must be at least three characters long\n";

        //validari lista de users
        if(entity.getTo().size()==0)
            errors+="The message must have a recipient\n";
        for(User u : entity.getTo()){
            if(u.getId() == null)
                errors+="The message must have a sender\n";
            if(u.getFirstName().equals(""))
                errors+="First name can't be empty\n";
            if(u.getLastName().equals(""))
                errors+="Last name can't be empty\n";
            allLetters=u.getFirstName().chars().allMatch(Character::isLetter);
            if(!allLetters)
                errors+="First name can contains only letters\n";
            allLetters=u.getLastName().chars().allMatch(Character::isLetter);
            if(!allLetters)
                errors+="Last name can contains only letters\n";
            if(u.getFirstName().length() < 3)
                errors+="First name must be at least three characters long\n";
            if(u.getLastName().length() < 3)
                errors+="Last name must be at least three characters long\n";
        }

        //validare mesaj
        if(entity.getMessage().equals(""))
            errors+="The message must have a text\n";
        if(entity.getMessage().length() <3)
            errors+="The message must be at least three characters long\n";

        if(!errors.equals(""))
            throw new ValidationException(errors);
    }
}
