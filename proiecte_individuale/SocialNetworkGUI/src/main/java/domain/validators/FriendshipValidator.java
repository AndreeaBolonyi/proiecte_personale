package socialnetwork.domain.validators;

import socialnetwork.domain.Friendship;

public class FriendshipValidator implements  Validator<Friendship>{
    /**
     *  validate a friendship
     * @param entity- Friendship objcet
     * @throws ValidationException if left id is equal with the right id from tuple
     */
    @Override
    public void validate(Friendship entity) throws ValidationException {
        String errors = "";
        if(entity.getId().getLeft().equals(entity.getId().getRight()))
            errors+="In a friendship must be two different persons\n";
        if(!errors.equals(""))
            throw new ValidationException(errors);
    }
}
